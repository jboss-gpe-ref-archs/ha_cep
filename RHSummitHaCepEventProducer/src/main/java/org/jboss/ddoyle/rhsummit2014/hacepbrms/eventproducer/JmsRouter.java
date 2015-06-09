package org.jboss.ddoyle.rhsummit2014.hacepbrms.eventproducer;

import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Fact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Router} which routes the events to a remote topic using JMS.
 *  
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class JmsRouter implements Router {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsRouter.class);
    
    private static final String REMOTE_CONNECTION_FACTORY_JNDI_NAME = "jms/RemoteConnectionFactory";
    
    private static final String REMOTE_EVENT_TOPIC_JNDI_NAME = "topic/event";
     
    private Connection connection;
    
    private Destination eventTopic;
    
    private Session session;
    
    private MessageProducer producer;

    public JmsRouter() {
        // Do a JNDI lookup of the remote topic.
        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            env.put(Context.PROVIDER_URL, "remote://localhost:4447");
            env.put(Context.SECURITY_PRINCIPAL, "guest");
            env.put(Context.SECURITY_CREDENTIALS, "guest@01");
            Context ctx = new InitialContext(env);
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup(REMOTE_CONNECTION_FACTORY_JNDI_NAME);
            connection = connectionFactory.createConnection("guest", "guest@01");

            eventTopic = (Destination) ctx.lookup(REMOTE_EVENT_TOPIC_JNDI_NAME);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(eventTopic);
            ctx.close();
        } catch (NamingException ne) {
            String errorMessage = "Unable to retrieve connection from remote Messaging System.";
            LOGGER.error(errorMessage, ne);
            throw new RuntimeException(errorMessage, ne);
        } catch (JMSException jmse) {
            String errorMessage = "Unable to create connection to remote Messaging System.";
            LOGGER.error(errorMessage, jmse);
            throw new RuntimeException(errorMessage, jmse);
        }
    }

    
    @Override
    public void route(Fact f) {
        try {
            Message factMessage = session.createObjectMessage(f);
            producer.send(factMessage);
        } catch (JMSException jmse) {
            String errorMessage = "Error sending Fact to Messaging System.";
            LOGGER.error(errorMessage, jmse);
            throw new RuntimeException(errorMessage, jmse);
        }
    }

    @PreDestroy
    public void close() {
        // close the JMS connection when we shut down.
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException jmse) {
                // Not much we can do. Log the error and continue.
                LOGGER.error("Error closing the JMS Connection to the Messaging System.", jmse);
            }
        }
    }
    
    

}
