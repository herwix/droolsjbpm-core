package com.iterranux.droolsjbpmCore.runtime.manager.impl;


import com.iterranux.droolsjbpmCore.runtime.manager.api.RuntimeManagerFactory;
import com.iterranux.droolsjbpmCore.internal.AbstractFactory;
import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.runtime.manager.api.SchedulerProvider;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.factory.InMemorySessionFactory;
import org.jbpm.runtime.manager.impl.factory.JPASessionFactory;
import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  Generic RuntimeManagerFactory that can execute any RuntimeManagerFactory that was injected via Spring.
 *  Allows for easy addition of new RuntimeManagers via plugins.
 */
public class GenericRuntimeManagerFactory extends AbstractFactory<RuntimeManagerFactory> {

    private TaskServiceFactory taskServiceFactory;

    /**
     * Creates a new RuntimeManager of the given Type for the supplied RuntimeEnvironment.
     *
     * @param runtimeManagerType The runtimeManagerType - accessibale via RuntimeManagerFactoryImpl.RUNTIME_MANAGER_TYPE
     * @param environment The runtime environment - can be created via the RuntimeEnvironmentFactory
     * @return runtimeManager
     */
    public RuntimeManager newRuntimeManager(String runtimeManagerType, RuntimeEnvironment environment){

        return newRuntimeManager(runtimeManagerType, environment, "default-"+runtimeManagerType);
    }

    /**
     * Creates a new RuntimeManager of the given Type with that unique identifier for the supplied RuntimeEnvironment.
     *
     * @param runtimeManagerType The runtimeManagerType - accessibale via RuntimeManagerFactoryImpl.RUNTIME_MANAGER_TYPE
     * @param environment The runtime environment - can be created via the RuntimeEnvironmentFactory
     * @param identifier Unique identifier - Instantiation with a non unique identifier will lead to an exception.
     * @return runtimeManager
     */
    public RuntimeManager newRuntimeManager(String runtimeManagerType, RuntimeEnvironment environment, String identifier){

        RuntimeManagerFactory runtimeManagerFactory = manufacturableBeans.get(runtimeManagerType);

        SessionFactory factory = getSessionFactory(environment);

        RuntimeManager manager = runtimeManagerFactory.newRuntimeManager( environment, factory, taskServiceFactory, identifier);

        initTimerService(environment, manager);
        ((AbstractRuntimeManager) manager).init();

        return manager;
    }

    protected SessionFactory getSessionFactory(RuntimeEnvironment environment) {
        SessionFactory factory = null;
        if (environment.usePersistence()) {
            factory = new JPASessionFactory(environment);
        } else {
            factory = new InMemorySessionFactory(environment);
        }

        return factory;
    }

    protected void initTimerService(RuntimeEnvironment environment, RuntimeManager manager) {
        if (environment instanceof SchedulerProvider) {
            GlobalSchedulerService schedulerService = ((SchedulerProvider) environment).getSchedulerService();
            if (schedulerService != null) {
                TimerService globalTs = new GlobalTimerService(manager, schedulerService);
                String timerServiceId = manager.getIdentifier()  + TimerServiceRegistry.TIMER_SERVICE_SUFFIX;
                // and register it in the registry under 'default' key
                TimerServiceRegistry.getInstance().registerTimerService(timerServiceId, globalTs);
                ((SimpleRuntimeEnvironment)environment).addToConfiguration("drools.timerService",
                        "new org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate(\""+timerServiceId+"\")");
            }
        }
    }

    public void setTaskServiceFactory(TaskServiceFactory taskServiceFactory) {
        this.taskServiceFactory = taskServiceFactory;
    }
}
