package org.jboss.ddoyle.brms.cep.ha.infinispan;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

import org.infinispan.commons.util.Util;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Provides a local CacheContainer in Infinispan 'Library-Mode'.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@ApplicationScoped
public class LocalCacheContainerFactory implements CacheContainerFactory {

    private static final String INFINISPAN_CONFIG_FILE_NAME = "infinispan/infinispan.xml";

    public synchronized CacheContainer getCacheContainer() {
        CacheContainer cacheContainer;
        // Retrieve Infinispan config file.
        InputStream infinispanConfigStream = this.getClass().getClassLoader().getResourceAsStream(INFINISPAN_CONFIG_FILE_NAME);
        try {
            try {
                cacheContainer = new DefaultCacheManager(infinispanConfigStream);
            } catch (IOException ioe) {
                throw new RuntimeException("Error loading Infinispan CacheManager.", ioe);
            }
        } finally {
            // Use Infinispan Util class to flush and close stream.
            Util.close(infinispanConfigStream);
        }
        return cacheContainer;
    }

}
