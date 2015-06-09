package org.jboss.ddoyle.rhsummit2014.hacepbrms.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.spi.KnowledgeHelper;
import org.jboss.ddoyle.brms.cep.ha.jms.router.FactRouter;
import org.jboss.ddoyle.brms.cep.ha.jms.router.Router;
import org.jboss.ddoyle.rhsummit2014.hacepbrms.model.Fact;
import org.kie.api.runtime.rule.Match;

/**
 * Factory implementation which creates {@link Command} objects.
 * <p/>
 * This factory can be used in rules to create the various {@link Command} implementations. It will take care of computing the unique id of
 * the {@link Command}, which will be used x-engine to identify the same commands. The injected {@link CommandIdGenerator} is responsible
 * for generating the ids from the Drools {@link Match}.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
@ApplicationScoped
public class SimpleCommandFactory implements CommandFactory {

    @Inject
    @FactRouter
    private Router<Fact> factRouter;

    public SystemOutCommand getSystemOutCommand(String message, KnowledgeHelper kHelper) {
        return new SystemOutCommand(CommandIdGenerator.generateId(kHelper.getMatch()), message);
    }

    public InsertFactCommand getInsertFactCommand(Fact fact, KnowledgeHelper kHelper) {
        /*
         * TODO: It might be nicer to inject the router into the fact itself, but because we're creating a new command here via the 'new'
         * keyword, that's kind of tricky. Using this solution for now.
         */
        return new InsertFactCommand(CommandIdGenerator.generateId(kHelper.getMatch()), fact, factRouter);
    }

}