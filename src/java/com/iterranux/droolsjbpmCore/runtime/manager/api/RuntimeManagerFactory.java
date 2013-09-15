package com.iterranux.droolsjbpmCore.runtime.manager.api;


import com.iterranux.droolsjbpmCore.internal.AbstractFactoryManufacturable;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;

/**
 * Simple interface that needs to be implemented if a new RuntimeManager type is to be introduced
 * to the GenericRuntimeManagerFactory. In addition to implementing this interface, the new factory
 * has to be injected into the GenericRuntimeManagerFactory. The static String RUNTIME_MANAGER_TYPE
 * should be overshadowed with a unique identifier for the given runtimeManager type.
 */
public interface RuntimeManagerFactory extends AbstractFactoryManufacturable {

    public RuntimeManager newRuntimeManager(RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier);

}
