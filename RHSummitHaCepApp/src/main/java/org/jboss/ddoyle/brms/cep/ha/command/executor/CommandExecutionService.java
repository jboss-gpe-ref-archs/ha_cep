package org.jboss.ddoyle.brms.cep.ha.command.executor;

import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.Command;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.IdempotentCommand;

/**
 * Responsible for executing {@link Command Commands}.
 * <p/>
 * This service provides idempotency of {@link Command Commands} if the {@link Command} is not an {@link IdempotentCommand}.
 * 
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public interface CommandExecutionService {
    
    public abstract Object execute(Command command);

}
