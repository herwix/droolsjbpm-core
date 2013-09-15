package com.iterranux.droolsjbpmCore.runtime.manager.impl;


import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;
import org.springframework.beans.factory.FactoryBean;

import javax.persistence.EntityManagerFactory;

public class DefaultRuntimeEnvironmentFactory implements FactoryBean<RuntimeEnvironment> {

    private EntityManagerFactory entityManagerFactory;

    private UserGroupCallback userGroupCallback;


    public RuntimeEnvironment getObject() throws Exception {
        return RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(entityManagerFactory)
                .get();

    }

    public Class<? extends RuntimeEnvironment> getObjectType() {
        return RuntimeEnvironment.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
}
