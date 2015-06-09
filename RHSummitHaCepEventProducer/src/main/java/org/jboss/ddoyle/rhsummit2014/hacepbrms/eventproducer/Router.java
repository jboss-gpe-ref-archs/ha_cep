package org.jboss.ddoyle.rhsummit2014.hacepbrms.eventproducer;

import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Fact;

/**
 * Interface for all <code>router</code> implementations in the system.
 *  
 *  
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public interface Router {

    public abstract void route(Fact fact);
    
    public abstract void close();
    
}
