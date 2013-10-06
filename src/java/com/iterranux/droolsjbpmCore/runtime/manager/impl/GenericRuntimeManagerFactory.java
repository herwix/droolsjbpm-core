/* Based on org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (c) 2013. Alexander Herwix
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * You can also obtain a commercial license. Contact: alex@herwix.com for further details.
 */

package com.iterranux.droolsjbpmCore.runtime.manager.impl;


import com.iterranux.droolsjbpmCore.internal.AbstractGenericFactory;
import com.iterranux.droolsjbpmCore.runtime.manager.api.RuntimeManagerFactory;
import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.runtime.manager.api.SchedulerProvider;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.factory.InMemorySessionFactory;
import org.jbpm.runtime.manager.impl.factory.JPASessionFactory;
import org.jbpm.runtime.manager.impl.tx.TransactionAwareSchedulerServiceInterceptor;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;

/**
 *  Generic RuntimeManagerFactory that can execute any RuntimeManagerFactory that was injected via Spring.
 *  Allows for easy addition of new RuntimeManagers via plugins.
 */
public class GenericRuntimeManagerFactory extends AbstractGenericFactory<RuntimeManagerFactory> {

    private TaskServiceFactory taskServiceFactory;

    /**
     * Creates a new RuntimeManager of the given Type for the supplied RuntimeEnvironment.
     *
     * @param runtimeManagerType The runtimeManagerType - accessibale via RuntimeManagerFactoryImpl.RUNTIME_MANAGER_TYPE
     * @param environment The runtime environment - can be created via the AbstractRuntimeEnvironmentFactory
     * @return runtimeManager
     */
    public RuntimeManager newRuntimeManager(String runtimeManagerType, RuntimeEnvironment environment){

        return newRuntimeManager(runtimeManagerType, environment, "default-"+runtimeManagerType);
    }

    /**
     * Creates a new RuntimeManager of the given Type with that unique identifier for the supplied RuntimeEnvironment.
     *
     * @param runtimeManagerType The runtimeManagerType - accessibale via RuntimeManagerFactoryImpl.RUNTIME_MANAGER_TYPE
     * @param environment The runtime environment - can be created via the AbstractRuntimeEnvironmentFactory
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

                if (!schedulerService.isTransactional()) {
                    schedulerService.setInterceptor(new TransactionAwareSchedulerServiceInterceptor(environment, schedulerService));
                }
            }
        }
    }

    public void setTaskServiceFactory(TaskServiceFactory taskServiceFactory) {
        this.taskServiceFactory = taskServiceFactory;
    }
}
