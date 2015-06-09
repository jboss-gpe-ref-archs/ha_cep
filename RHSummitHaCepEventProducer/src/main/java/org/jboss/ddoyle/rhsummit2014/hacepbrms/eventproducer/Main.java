package org.jboss.ddoyle.rhsummit2014.hacepbrms.eventproducer;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class which produces Airport Baggage events.
 *  
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class Main {
    
    //private static final int EVENT_ROUTE_WAIT_TIME = 5000;
    private static final int EVENT_ROUTE_WAIT_TIME = 1000;
    
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    private static Router eventRouter;
    
    public static void main(String[] args) {
        LOGGER.info("Initializing Router.");
        eventRouter = new JmsRouter();
        
        //String eventsFileUrl = Main.class.getClassLoader().getResource("events.csv").getFile();
        //File eventsFile = new File(eventsFileUrl);
        InputStream eventsFileStream = Main.class.getClassLoader().getResourceAsStream("events.csv");
        
        LOGGER.info("Loading events from file.");
        
        List<Event> events = FactsLoader.loadEvents(eventsFileStream);
        
        
        for (Event nextEvent: events) {
            LOGGER.info("Routing event to CEP system.");
            eventRouter.route(nextEvent);
            try {
                Thread.sleep(EVENT_ROUTE_WAIT_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("Got interrupted during sleep. Resestting interrupt on thread,");
                Thread.currentThread().interrupt();
            }
        }
        
        eventRouter.close();
        
    }

}
