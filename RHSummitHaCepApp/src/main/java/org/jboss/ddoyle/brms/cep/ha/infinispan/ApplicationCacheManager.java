package org.jboss.ddoyle.brms.cep.ha.infinispan;

import org.infinispan.commons.api.BasicCache;


/**
 * Interface for application abstraction layer into the ISPN caches.
 *  
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public interface ApplicationCacheManager {
    
    public abstract <K, V> BasicCache<K,V> getCache();
    
    public abstract <K, V> BasicCache<K,V> getCache(String name);
    
    public abstract void startCache();
    
    public abstract void startCache(final String cacheName);
    
    public abstract void stopCache();
    
    public abstract void stopCache(final String cacheName);
    
}
