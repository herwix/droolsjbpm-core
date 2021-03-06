/* Based on org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory
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

package com.iterranux.droolsjbpmCore.task.impl;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.task.api.UserGroupCallback;

import javax.persistence.EntityManagerFactory;

/**
 * Adapted LocalTaskService to work with Spring DI
 */
public class SpringTaskServiceFactory implements TaskServiceFactory {

    private EntityManagerFactory entityManagerFactory;

    private UserGroupCallback userGroupCallback;


    @Override
    public TaskService newTaskService() {
        if (entityManagerFactory != null) {

            TaskService internalTaskService =   HumanTaskServiceFactory.newTaskServiceConfigurator()
                    .transactionManager(new JbpmJTATransactionManager())
                    .entityManagerFactory(entityManagerFactory)
                    .userGroupCallback(userGroupCallback)
                    .getTaskService();

            return internalTaskService;
        } else {
            return null;
        }
    }

    @Override
    public void close() {

    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }
}
