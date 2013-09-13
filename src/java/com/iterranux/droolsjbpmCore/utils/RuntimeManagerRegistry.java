package com.iterranux.droolsjbpmCore.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public enum RuntimeManagerRegistry {
    INSTANCE;

    private static final Log log = LogFactory.getLog(RuntimeManagerRegistry.class.getName());

    private Collection<String> runtimeManagerImplementations = Collections.synchronizedSet(new LinkedHashSet<String>());

    /**
     * Get a list of fully qualified classNames that were registered as RuntimeManager implementations.
     * This allows plugins to register new RuntimeManager implementations to extend this service.
     *
     * @return List of fully qualified classNames that were registered as RuntimeManager implementations.
     */
    public synchronized Collection<String> getRuntimeManagerImpl(){
        return runtimeManagerImplementations;
    }

    /**
     * Register the given Class as a RuntimeManager implementation
     * @param clazz RuntimeManager class to be registered
     */
    public synchronized void registerRuntimeManagerImpl(final Class clazz){

        final String className = clazz.getName();
        runtimeManagerImplementations.add(className);
        log.debug("RuntimeManagerRegistry: Registered "+className);
    }

    /**
     * Register the List of ClassNames as a RuntimeManager implementations
     * @param list List of fully qualified ClassNames to be registered.
     */
    public synchronized void registerRuntimeManagerImpl(Collection<String> list){

        runtimeManagerImplementations.addAll(list);
        log.debug("RuntimeManagerRegistry: Registered "+list);
    }



}
