package org.jboss.ddoyle.brms.cep.ha.infinispan;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@Listener
public class CacheEntryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheEntryListener.class);

    @CacheEntryCreated
    public void handleCacheEntryCreatedEvent(CacheEntryCreatedEvent event) {
        // We just listen to the 'pre' events.
        if (event.isPre()) {
            LOGGER.info("Cache Entry Created with key: " + event.getKey());
        }
    }

    @CacheEntryModified
    public void handleCacheEntryModifiedEvent(CacheEntryModifiedEvent event) {
        // We just listen to the 'pre' events.
        if (event.isPre()) {
            /*
             * Check that it is a modification and not a new entry. In the current version of Infinispan, a CacheEntryModified event is also
             * fired when a new entry is created. When 'isPre() == true' and the 'getValue() == null', the event is actually a creation.
             * This model has changed in Infinispan version 5.3.0.
             */
            if (event.getValue() != null) {
                //We now know this is a modifcation.
                LOGGER.info("Cache Entry Modified with key: " + event.getKey());
            }
        }
    }

    @CacheEntryRemoved
    public void handleCacheEntryRemovedEvent(CacheEntryRemovedEvent event) {
        LOGGER.info("Cache Entry Removed with key: " + event.getKey());
    }
}
