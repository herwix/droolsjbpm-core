package com.iterranux.droolsjbpmCore.runtime.environment.impl;


import com.iterranux.droolsjbpmCore.internal.ResourceTypeIOFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;

import javax.persistence.EntityManagerFactory;
import java.io.File;

/**
 * Abstract Factory that allows for the easy creation of RuntimeEnvironments.
 * EntityManagerFactory and UserGroupCallback are (automatically) configured through spring.
 *
 */
public abstract class AbstractRuntimeEnvironmentFactory {

    private EntityManagerFactory entityManagerFactory;

    private UserGroupCallback userGroupCallback;

    private RegisterableItemsFactory registerableItemsFactory;

    /**
     * Simple RuntimeEnvironmentBuilder factory method that can be used to set up a custom RuntimeEnvironment
     * via the builder methods. UserGroupCallback and EntityManagerFactory are already configured.
     *
     * @return RuntimeEnvironmentBuilder
     */
    public RuntimeEnvironmentBuilder newDefaultRuntimeEnvironmentBuilder(){

        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(entityManagerFactory);

        if (registerableItemsFactory != null){
            builder.registerableItemsFactory(registerableItemsFactory);
        }

        return builder;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setRegisterableItemsFactory(RegisterableItemsFactory registerableItemsFactory) {
        this.registerableItemsFactory = registerableItemsFactory;
    }

}
