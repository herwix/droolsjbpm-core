package com.iterranux.droolsjbpmCore.task

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

class TaskIntegrationTests extends GroovyTestCase{

    def singletonRuntimeManager

    void testHumanTaskProcess() {
        def manager = singletonRuntimeManager
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
    }

}
