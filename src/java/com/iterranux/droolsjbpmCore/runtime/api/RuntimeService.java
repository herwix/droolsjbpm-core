package com.iterranux.droolsjbpmCore.runtime.api;


import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.RuntimeEnvironment;

import java.util.List;

public interface RuntimeService {

    RuntimeEnvironment getRuntimeEnvironment();

    RuntimeManager createRuntimeManager();

    RuntimeManager createRuntimeManager( RuntimeEnvironment runtimeEnvironment, final String identifier);

    ProcessInstance startProcess(final String name);



}
