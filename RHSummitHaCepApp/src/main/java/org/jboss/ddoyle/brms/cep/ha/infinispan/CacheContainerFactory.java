package org.jboss.ddoyle.brms.cep.ha.infinispan;

import org.infinispan.manager.CacheContainer;

/**
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public interface CacheContainerFactory {

    public abstract CacheContainer getCacheContainer();
    
}
