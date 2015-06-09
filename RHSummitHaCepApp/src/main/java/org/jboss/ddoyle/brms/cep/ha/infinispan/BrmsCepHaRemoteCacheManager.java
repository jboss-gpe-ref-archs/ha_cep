package org.jboss.ddoyle.brms.cep.ha.infinispan;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;
import org.jboss.ddoyle.brms.cep.ha.cdi.HotRod;

/**
 * {@link ApplicationCacheManager} for remote caches.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@HotRod
@ApplicationScoped
public class BrmsCepHaRemoteCacheManager implements ApplicationCacheManager {

    private static final String APPLICATION_DEFAULT_CACHE_NAME = "MyCoolCache";
    
    private BasicCacheContainer cacheManager;
    
    @PostConstruct
    private void init() {
        cacheManager = new RemoteCacheManager();
    }
    
    @Override
    public <K, V> BasicCache<K, V> getCache() {
        return cacheManager.getCache(APPLICATION_DEFAULT_CACHE_NAME);
    }

    @Override
    public <K, V> BasicCache<K, V> getCache(String name) {
        return cacheManager.getCache(name);
    }

    @Override
    public void startCache() {
        getCache().start();
        
    }

    @Override
    public void startCache(String cacheName) {
        getCache(cacheName).start();
        
    }

    @Override
    public void stopCache() {
        getCache().stop();
        
    }

    @Override
    public void stopCache(String cacheName) {
        getCache(cacheName).stop();
    }

}
