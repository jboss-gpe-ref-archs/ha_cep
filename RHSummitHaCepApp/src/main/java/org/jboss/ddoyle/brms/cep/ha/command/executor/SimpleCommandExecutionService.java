package org.jboss.ddoyle.brms.cep.ha.command.executor;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.ddoyle.brms.cep.ha.cdi.Infinispan;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of the {@link CommandExecutionService} interface. Simply calls <code>execute</code> on the {@link Command}.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@ApplicationScoped
@Infinispan
public class SimpleCommandExecutionService implements CommandExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommandExecutionService.class);
    
    public SimpleCommandExecutionService() {
    }

    @Override
    public Object execute(Command command) {
        LOGGER.debug("EXECUTING COMMAND: " + command.getId() + "\n");
        return command.execute();
    }

}
