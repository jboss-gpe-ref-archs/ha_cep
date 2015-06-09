package org.jboss.ddoyle.rhsummit2014.hacepbrms.command;

import java.io.Serializable;

public interface Command extends Serializable {
    
    public abstract String getId();
    
    public abstract Object execute();

}
