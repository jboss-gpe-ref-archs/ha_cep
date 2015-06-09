package org.jboss.ddoyle.brms.cep.ha.command.dispatch;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.commons.api.BasicCache;
import org.jboss.ddoyle.brms.cep.ha.cdi.HotRod;
import org.jboss.ddoyle.brms.cep.ha.cdi.Infinispan;
import org.jboss.ddoyle.brms.cep.ha.command.executor.CommandExecutionService;
import org.jboss.ddoyle.brms.cep.ha.command.executor.SimpleCommandExecutionService;
import org.jboss.ddoyle.brms.cep.ha.infinispan.ApplicationCacheManager;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.Command;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.command.IdempotentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatches commands to the {@link SimpleCommandExecutionService}.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@ApplicationScoped
@Infinispan
public class InfinispanIdempotantCommandDispatcher implements CommandDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanIdempotantCommandDispatcher.class);
    
    /**
     * TTL of the {@link Command} objects determines how long they will be stored in the cache. I.e. when a {@link Command} object is
     * removed from the cache and the same {@link Command} enters the service, it will be re-executed.
     */
    private static final long COMMAND_TTL = 6;
    private static final TimeUnit COMMAND_TTL_TIMEUNIT = TimeUnit.HOURS;

    @Inject
    private ApplicationCacheManager cacheManager;

    private BasicCache<String, Command> commandsCache;

    @Inject
    private @Infinispan CommandExecutionService commandExecutionService;
    
    @PostConstruct
    public void initialize() {
        commandsCache = cacheManager.getCache();
    }

    @Override
    public void dispatch(Command command) {
        LOGGER.trace("Dispatching Command to CommandExecutionService.");
        if (command instanceof IdempotentCommand) {
            commandExecutionService.execute(command);
        } else {
            Command oldCommand = commandsCache.putIfAbsent(command.getId(), command, COMMAND_TTL, COMMAND_TTL_TIMEUNIT);
            if (oldCommand == null) {
                LOGGER.debug("INSERTED COMMAND with ID: '" + command.getId() + "' into cache.\n");
                commandExecutionService.execute(command);
            } else {
                LOGGER.debug("DISCARDING COMMAND with ID: '" + command.getId() + "' as it has already been executed earlier.\n");
            }

        }
        
    }

}
