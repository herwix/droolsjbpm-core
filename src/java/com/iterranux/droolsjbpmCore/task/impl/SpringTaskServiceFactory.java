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
