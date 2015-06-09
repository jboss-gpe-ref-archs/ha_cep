package org.jboss.ddoyle.brms.cep.ha.jms.router;

public interface Router<T> {
    
    public abstract void route(T t) throws RouteException;

}
