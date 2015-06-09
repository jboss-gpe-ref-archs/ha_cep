package org.jboss.ddoyle.brms.cep.ha.infinispan;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.jboss.ddoyle.brms.cep.ha.cdi.Eager;

/**
 * {@link Cache} manager for ISPN caches.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */

@Eager
@ApplicationScoped
public class BrmsCepHaApplicationCacheManager implements ApplicationCacheManager {
    
    private static final String APPLICATION_DEFAULT_CACHE_NAME = "commandsCache";
    
    private CacheContainer cacheContainer;
    
    public BrmsCepHaApplicationCacheManager() {
    }
    
    @PostConstruct
    public void initialize() {
        cacheContainer = new LocalCacheContainerFactory().getCacheContainer();
        cacheContainer.getCache(APPLICATION_DEFAULT_CACHE_NAME).addListener(new CacheEntryListener());
    }
        
    public <K, V> Cache<K, V> getCache() {
        return cacheContainer.getCache(APPLICATION_DEFAULT_CACHE_NAME);
    }
    
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return cacheContainer.getCache(APPLICATION_DEFAULT_CACHE_NAME);
    }

    public void startCache() {
        getCache().start();
    }

    public void startCache(String cacheName) {
        getCache(cacheName).start();
    }

    public void stopCache() {
        getCache().stop();
    }

    public void stopCache(String cacheName) {
        getCache(cacheName).stop();
    }
    
    /*
     * Cleanly close the CacheContainer on shutdown.
     */
    @PreDestroy
    public void stopCacheContainer() {
        if (cacheContainer != null) {
            cacheContainer.stop();
        }
    }

}
