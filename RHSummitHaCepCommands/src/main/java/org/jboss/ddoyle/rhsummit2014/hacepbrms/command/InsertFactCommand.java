package org.jboss.ddoyle.rhsummit2014.hacepbrms.command;

import org.jboss.ddoyle.brms.cep.ha.jms.router.RouteException;
import org.jboss.ddoyle.brms.cep.ha.jms.router.Router;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Fact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InsertFactCommand extends AbstractCommand {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertFactCommand.class);

    private final Fact fact;

    private final Router<Fact> router;

    public InsertFactCommand(String id, Fact fact, Router<Fact> router) {
        super(id);
        this.fact = fact;
        this.router = router;
    }

    @Override
    public Object execute() {
        boolean routed = false;
        int retryCount = 0;
        while (routed == false && retryCount < 5) {
            try {
                router.route(fact);
                routed = true;
            } catch (RouteException re) {
                String errorMessage = "Error routing Fact. Retrying.";
                LOGGER.warn(errorMessage, re);
                retryCount++;
            }
        }
        if (routed == false) {
            // Fact has not been properly routed. Log an error and discard the Fact.
            // TODO: Fact could also be printed in the log or stored on disk somewhere if discarding is not allowed.
            String errorMessage = "Error routing fact to destinaton. Fact will be discarded!!!";
            LOGGER.error(errorMessage);
        }
        return null;
    }
}
