package org.jboss.ddoyle.brms.cep.ha.input;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.time.SessionPseudoClock;
import org.jboss.ddoyle.brms.cep.ha.drools.session.SessionProducer;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Event;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Fact;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@PseudoClock
public class PseudoClockFactInserter implements FactInserter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PseudoClockFactInserter.class);

    @Inject
    private SessionProducer sessionProducer;

    private KieSession kieSession;

    public PseudoClockFactInserter() {
    }

    @PostConstruct
    private void postConstruct() {
        kieSession = sessionProducer.getKieSession();
        SessionClock clock = kieSession.getSessionClock();
        SessionPseudoClock pseudoClock = (SessionPseudoClock) clock;
        long currentTime = pseudoClock.getCurrentTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        LOGGER.info("postConstruct() pseudoClock starting at the following time :"+sdf.format(new Date(currentTime)));
    }

    public FactHandle insert(final Fact fact) {
        return insert(DEFAULT_STREAM, fact);
    }

    public FactHandle insert(String stream, Fact fact) {
        LOGGER.info("Inserting fact with id: '" + fact.getId() + "' into stream: " + stream);
        SessionClock clock = kieSession.getSessionClock();
        if (!(clock instanceof SessionPseudoClock)) {
            String errorMessage = "This fact inserter can only be used with KieSessions that use a SessionPseudoClock";
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        SessionPseudoClock pseudoClock = (SessionPseudoClock) clock;
        EntryPoint ep = getKieSession().getEntryPoint(stream);

        // Let's first insert the fact.
        LOGGER.debug("Inserting fact: " + fact.toString());
        FactHandle factHandle = ep.insert(fact);
        
        

        // And then advance the clock
        /*
         * TODO: Checking the clock time and advancing it should be an atomic operation. Hence, when using multiple fact inserters, multiple
         * threads, etc. one has to implement an wrapper for the clock which advances it atomically. Actually, that would be a nice
         * improvement to the SessionClock: advanceTo .....
         */
        // We only need to advance the time when dealing with Events. Our facts don't have timestamps.
        if (fact instanceof Event) {

            synchronized (this) {

                long advanceTime = ((Event) fact).getTimestamp().getTime() - pseudoClock.getCurrentTime();
                if (advanceTime > 0) {
                    LOGGER.info("Advancing the PseudoClock with " + advanceTime + " milliseconds.");
                    pseudoClock.advanceTime(advanceTime, TimeUnit.MILLISECONDS);
                } else {
                    LOGGER.info("Not advancing time. Fact timestamp is '" + ((Event) fact).getTimestamp().getTime() + "', PseudoClock timestamp is '"
                            + pseudoClock.getCurrentTime() + "'.");
                }
            }
        }
        LOGGER.info("Number of FactHandles in Entry-Point: " + ep.getFactHandles().size());
        LOGGER.info("Firing rules!");
        kieSession.fireAllRules();
        
        return factHandle;
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    public void setKieSession(KieSession kieSession) {
        this.kieSession = kieSession;
    }

}
