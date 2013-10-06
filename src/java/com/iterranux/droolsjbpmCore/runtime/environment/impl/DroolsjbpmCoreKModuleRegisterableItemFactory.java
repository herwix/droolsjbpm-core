/* Based on org.jbpm.runtime.manager.impl.KModuleRegisterableItemsFactory
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


import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.util.CDIHelper;
import org.drools.core.util.StringUtils;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroolsjbpmCoreKModuleRegisterableItemFactory extends DroolsjbpmCoreDefaultRegisterableItemsFactory {

    protected KieContainer kieContainer;
    protected String kieSessionName;

    DroolsjbpmCoreKModuleRegisterableItemFactory(KieContainer kieContainer, String kieSessionName){
        super();
        this.kieContainer = kieContainer;
        this.kieSessionName = kieSessionName;
    }

    DroolsjbpmCoreKModuleRegisterableItemFactory(KieContainer kieContainer, String kieSessionName, AuditEventBuilder auditEventBuilder){
        super();
        this.kieContainer = kieContainer;
        this.kieSessionName = kieSessionName;
        setAuditBuilder(auditEventBuilder);
    }

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        KieSessionModel ksessionModel = null;
        if(StringUtils.isEmpty(kieSessionName)) {
            ksessionModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieSession();
        } else {
            ksessionModel = ((KieContainerImpl)kieContainer).getKieSessionModel(kieSessionName);
        }

        if (ksessionModel == null) {
            throw new IllegalStateException("Cannot find ksession with name " + kieSessionName);
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ksession", runtime.getKieSession());
        parameters.put("taskService", runtime.getKieSession());
        parameters.put("runtimeManager", ((RuntimeEngineImpl)runtime).getManager());
        try {

            CDIHelper.wireListnersAndWIHs(ksessionModel, runtime.getKieSession(), parameters);
        } catch (Exception e) {
            // use fallback mechanism
            CDIHelper.wireListnersAndWIHs(ksessionModel, runtime.getKieSession());
        }

        return super.getWorkItemHandlers(runtime);
    }

    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        return super.getProcessEventListeners(runtime);
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        return super.getAgendaEventListeners(runtime);
    }

    @Override
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(
            RuntimeEngine runtime) {
        return super.getWorkingMemoryEventListeners(runtime);
    }
}
