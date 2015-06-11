package org.jboss.ddoyle.brms.cep.ha.input.messaging;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ddoyle.brms.cep.ha.input.FactInserter;
import org.jboss.ddoyle.brms.cep.ha.input.PseudoClock;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Fact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple listener which listens to a HornetQ topic on a HornetQ server.
 * <p/>
 * You might want to implement this with something more sophisticated like Apache Camel, or run use an MDB on a EE server, but this servers
 * the purpose for now.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@ApplicationScoped
public class EventListener {

    private static final String REMOTE_CONNECTION_FACTORY_JNDI_NAME = "jms/RemoteConnectionFactory";

    private static final String REMOTE_EVENT_TOPIC_JNDI_NAME = "topic/event";

    private static final String CONNECTION_CLIENT_ID_PROPERTY_NAME = "rhsummit2014.hornetq.client.id";

    private static final long BLOCKING_RECEIVE_TIMEOUT = 1000;

    // TODO: Topic subscriber name should come from a configuration file outside of the system.
    private static final String DURABLE_TOPIC_SUBSCRIBER_NAME = "test";

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    private boolean doStop = false;
    private boolean stopped = true;

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Topic eventTopic;

    @Inject @PseudoClock
    private FactInserter factInserter;

    private String clientId;

    public EventListener() {
    }

    @PostConstruct
    public void initialize() {
        clientId = System.getProperty(CONNECTION_CLIENT_ID_PROPERTY_NAME);
        if (clientId == null || "".equals(clientId)) {
            throw new IllegalStateException(
                    "Client-ID property '" + CONNECTION_CLIENT_ID_PROPERTY_NAME + "' is mandatory to properly create a durable topic subscriber. Please configure this system property.");
        }

        LOGGER.info("Initializing EventListener: " + this+"\n\tfactInserter = "+factInserter);

        // Do a JNDI lookup of the remote topic.
        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            env.put(Context.PROVIDER_URL, "remote://localhost:4447");
            env.put(Context.SECURITY_PRINCIPAL, "guest");
            env.put(Context.SECURITY_CREDENTIALS, "guest@01");
            Context ctx = new InitialContext(env);
            connectionFactory = (ConnectionFactory) ctx.lookup(REMOTE_CONNECTION_FACTORY_JNDI_NAME);
            connection = connectionFactory.createConnection("guest", "guest@01");
            // This should actually be set on a client-specific connectionfactory id done properly.
            connection.setClientID(clientId);
            eventTopic = (Topic) ctx.lookup(REMOTE_EVENT_TOPIC_JNDI_NAME);
            // Start the connection, otherwise we will not receive messages.
            connection.start();
            ctx.close();
        } catch (NamingException ne) {
            String errorMessage = "Unable to retrieve connection from remote Messaging System.";
            LOGGER.error(errorMessage, ne);
            throw new RuntimeException(errorMessage, ne);
        } catch (JMSException jmse) {
            String errorMessage = "Error creating connection to remote Messaging System.";
            LOGGER.error(errorMessage, jmse);
            throw new RuntimeException(errorMessage, jmse);
        }
    }

    public void start() {
        // Reset the doStop.
        LOGGER.info("Starting JMS Listener.");
        doStop = false;
        stopped =false;
        Session session = null;
        TopicSubscriber topicSubscriber = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topicSubscriber = session.createDurableSubscriber(eventTopic, DURABLE_TOPIC_SUBSCRIBER_NAME);
            
            // A bit dirty doing a blocking wait, but sufficient for this example.
            while (!doStop) {
                
                try {
                    Message message = topicSubscriber.receive(BLOCKING_RECEIVE_TIMEOUT);
                    // We expect the message to be an object message.
                    if (message != null) {
                        if (message instanceof ObjectMessage) {
                            Object payload = ((ObjectMessage) message).getObject();
                            // We expect the payload to be a fact.
                            if (payload instanceof Fact) {
                                // Insert the fact into the session.
                                factInserter.insert((Fact) payload);
                            } else {
                                // Log an error and discard the message.
                                LOGGER.error("Expected payload of type Fact but received payload of type '"
                                        + payload.getClass().getCanonicalName() + "'. Discarding payload.");
                            }
                        } else {
                            // Log an error and discard the message. There should not be any other messages being posted to this topic.
                            LOGGER.error("Expected ObjectMessage but received message of type '" + message.getClass().getCanonicalName()
                                    + "'. Discarding message!");
                        }
                    }
                } catch (JMSException jmse) {
                    String errorMessage = "Error receiving message from topic.";
                    LOGGER.error(errorMessage, jmse);
                    // TODO: We might not want to throw a runtimeexeption immediately as it probably stops the entire system.
                    throw new RuntimeException(errorMessage, jmse);
                }
            }
        } catch (JMSException jmse) {
            String errorMessage = "Error creating session and subscribing to topic.";
            LOGGER.error(errorMessage, jmse);
            throw new RuntimeException(errorMessage, jmse);
        } catch (Throwable t) {
            LOGGER.error("Got a throwable!!!", t);
            throw new RuntimeException(t);
        } finally {
            if (topicSubscriber != null) {
                try {
                    LOGGER.info("Closing topic subscriber.");
                    topicSubscriber.close();
                } catch (JMSException jmse) {
                    LOGGER.error("Error while unsubscribing topicSubscriber.", jmse);
                    //Nothing more we can do, swallowing exception.
                }
            }
            if (session != null) {
                try {
                    LOGGER.info("Closing session.");
                    session.close();
                } catch (JMSException jmse) {
                    LOGGER.error("Error while closing session.", jmse);
                    //Nothing more we an do, swallowing exception.
                }
            }
            stopped = true;
        }

    }

    @PreDestroy
    private void destroy() {
        try {
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        } catch (JMSException jmse) {
            String errorMessage = "Unable to properly close the connection with the remote Messaging System.";
            LOGGER.warn(errorMessage, jmse);
            // Don't take any further actions as it this is not a serious showstopper for the application.
        }
    }

    /**
     * Sets the stop marker to stop receiving events.
     */
    public void stop() {
        doStop = true;
        //Wait till we've actually stopped.
        int retries = 0;
        while (!stopped && retries < 10) {
            //TODO: Very dirty hack. This can potentially hang indefinitely.
            try {
                Thread.sleep(100);
                retries++;
            } catch (InterruptedException ie) {
                String errorMessage = "Thread got interrupted, resetting interrupt on thread.";
                LOGGER.error(errorMessage, ie);
                Thread.currentThread().interrupt();
            }
        }
        
    }

}
