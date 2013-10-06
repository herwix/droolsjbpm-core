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

package com.iterranux.droolsjbpmCore.task

import com.iterranux.droolsjbpmCore.runtime.manager.impl.SingletonRuntimeManagerFactory
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.manager.RuntimeManager
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.task.TaskService
import org.kie.api.task.model.TaskSummary
import org.kie.internal.runtime.manager.RuntimeEnvironment

import static org.junit.Assert.*

class TaskIntegrationTests extends GroovyTestCase{

    def droolsjbpmKmoduleRuntimeEnvironmentFactory

    def droolsjbpmRuntimeManagerFactory

    def droolsjbpmCoreUtils

    protected RuntimeManager createRuntimeManager(){
        RuntimeEnvironment runtimeEnvironment = droolsjbpmKmoduleRuntimeEnvironmentFactory.newRuntimeEnvironment(droolsjbpmCoreUtils.getReleaseIdForGrailsModule('droolsjbpmCore'))

        return  droolsjbpmRuntimeManagerFactory.newRuntimeManager(SingletonRuntimeManagerFactory.RUNTIME_MANAGER_TYPE,runtimeEnvironment,"test")
    }

    void testHumanTaskProcess() {
        def manager = createRuntimeManager()
        RuntimeEngine engine = manager.getRuntimeEngine();
        assertNotNull(engine);

        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);

        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument");

        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        TaskService taskService = engine.getTaskService();

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        long taskId = tasks.get(0).getId();

        taskService.start(taskId, "salaboy");
        taskService.complete(taskId, "salaboy", null);

        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        tasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        taskId = tasks.get(0).getId();

        taskService.start(taskId, "translator");
        taskService.complete(taskId, "translator", null);

        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        tasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        taskId = tasks.get(0).getId();

        taskService.start(taskId, "reviewer");
        taskService.complete(taskId, "reviewer", null);

        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        manager.close()
    }

}
