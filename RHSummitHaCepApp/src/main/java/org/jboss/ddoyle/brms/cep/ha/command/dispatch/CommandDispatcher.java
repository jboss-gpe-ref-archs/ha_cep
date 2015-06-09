package org.jboss.ddoyle.brms.cep.ha.command.dispatch;

import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.Command;


/**
 * Dispatches {@link Command} objects.
 * <p/>
 * To be used within rules to dispatch various {@link Command Commands} from the rules to the {@link Command} execution layer.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public interface CommandDispatcher {

    public abstract void dispatch(Command command);

}
