/* Based on org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory
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

package com.iterranux.droolsjbpmCore.runtime.environment.impl;

import com.iterranux.droolsjbpmCore.task.api.LocalHTWorkItemHandlerFactory;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.kie.internal.task.api.EventService;

public class DroolsjbpmCoreDefaultRegisterableItemsFactory extends DefaultRegisterableItemsFactory {

    protected LocalHTWorkItemHandlerFactory localHTWorkItemHandlerFactory;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected WorkItemHandler getHTWorkItemHandler(RuntimeEngine runtime) {

        ExternalTaskEventListener listener = new ExternalTaskEventListener();
        listener.setRuntimeManager(((RuntimeEngineImpl)runtime).getManager());

        LocalHTWorkItemHandler humanTaskHandler = localHTWorkItemHandlerFactory.newLocalHTWorkItemHandler();
        humanTaskHandler.setRuntimeManager(((RuntimeEngineImpl)runtime).getManager());
        if (runtime.getTaskService() instanceof EventService) {
            ((EventService)runtime.getTaskService()).registerTaskLifecycleEventListener(listener);
        }

        if (runtime instanceof Disposable) {
            ((Disposable)runtime).addDisposeListener(new DisposeListener() {

                @Override
                public void onDispose(RuntimeEngine runtime) {
                    if (runtime.getTaskService() instanceof EventService) {
                        ((EventService)runtime.getTaskService()).clearTaskLifecycleEventListeners();
                        ((EventService)runtime.getTaskService()).clearTasknotificationEventListeners();
                    }
                }
            });
        }
        return humanTaskHandler;
    }

    public void setLocalHTWorkItemHandlerFactory(LocalHTWorkItemHandlerFactory localHTWorkItemHandlerFactory) {
        this.localHTWorkItemHandlerFactory = localHTWorkItemHandlerFactory;
    }

}
