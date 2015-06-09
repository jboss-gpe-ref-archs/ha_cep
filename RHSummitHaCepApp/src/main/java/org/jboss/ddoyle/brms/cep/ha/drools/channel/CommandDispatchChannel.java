package org.jboss.ddoyle.brms.cep.ha.drools.channel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.ddoyle.brms.cep.ha.cdi.Infinispan;
import org.jboss.ddoyle.brms.cep.ha.command.dispatch.CommandDispatcher;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.Command;
import org.kie.api.runtime.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>Drools</code> {@link Channel} implementation which dispatches {@link Command Commands}.
 *  
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@ApplicationScoped
public class CommandDispatchChannel implements Channel {

    public static final String CHANNEL_ID = "commandDispatchChannel";
        
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatchChannel.class);
    
    @Inject
    private @Infinispan CommandDispatcher cd;
    
    @Override
    public void send(Object object) {
        if (!(object instanceof Command)) {
            String errorMessage = " This channel implementation can only process Command objects, but received object of type: '" + object.getClass().getCanonicalName() + "'. Discarding object.";
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        cd.dispatch((Command) object);
    }

}
