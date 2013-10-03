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
