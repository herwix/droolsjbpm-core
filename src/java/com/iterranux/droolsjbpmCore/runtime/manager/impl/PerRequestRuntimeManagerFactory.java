package com.iterranux.droolsjbpmCore.runtime.manager.impl;


import com.iterranux.droolsjbpmCore.runtime.manager.api.RuntimeManagerFactory;
import org.jbpm.runtime.manager.impl.PerRequestRuntimeManager;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.springframework.stereotype.Component;

@Component
public class PerRequestRuntimeManagerFactory implements RuntimeManagerFactory {

    public final static String RUNTIME_MANAGER_TYPE = "PER_REQUEST";

    @Override
    public RuntimeManager newRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier) {
        return new PerRequestRuntimeManager(environment, factory, taskServiceFactory, identifier);
    }
}
