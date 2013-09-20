package com.iterranux.droolsjbpmCore


import grails.transaction.Transactional
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl
import org.kie.api.io.ResourceType
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.KieSessionConfiguration
import org.kie.api.runtime.manager.RuntimeManager
import org.kie.api.runtime.process.ProcessInstance
import org.kie.api.task.TaskService
import org.kie.internal.io.ResourceFactory
import org.kie.internal.process.CorrelationKey
import org.kie.internal.runtime.manager.RuntimeEnvironment
import org.kie.internal.task.api.UserGroupCallback

import javax.persistence.EntityManagerFactory

@Transactional
class DroolsjbpmService {

    EntityManagerFactory droolsjbpmEntityManagerFactory

    RuntimeEnvironment getRuntimeEnvironment(){

    }

    RuntimeManager getRuntimeManager(){

    }



    ProcessInstance startProcess(String name){

    }

    RuntimeEnvironment runtimeEnvironment(){

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(getUserGroupCallback())
                .entityManagerFactory(droolsjbpmEntityManagerFactory)
                .get();

        return environment
    }


    private static UserGroupCallback getUserGroupCallback(){

        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");

        return new JBossUserGroupCallbackImpl(properties);
    }

}
