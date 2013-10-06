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


import javax.persistence.EntityManagerFactory;

import grails.util.Holders;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.task.api.UserGroupCallback;

/**
 * Abstract Factory that allows for the easy creation of RuntimeEnvironments.
 * EntityManagerFactory and UserGroupCallback are (automatically) configured through spring.
 *
 */
public abstract class AbstractRuntimeEnvironmentFactory {

    protected EntityManagerFactory entityManagerFactory;

    protected UserGroupCallback userGroupCallback;

    protected RegisterableItemsFactoryFactory registerableItemsFactoryFactory;


    /**
     * Simple RuntimeEnvironmentBuilder factory method that can be used to set up a custom RuntimeEnvironment
     * via the builder methods. UserGroupCallback, EntityManagerFactory, Classloader are already configured.
     *
     * @return RuntimeEnvironmentBuilder
     */
    public RuntimeEnvironmentBuilder newDefaultRuntimeEnvironmentBuilder(){

        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(entityManagerFactory)
                .classLoader(Holders.getGrailsApplication().getClassLoader());

        return builder;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setRegisterableItemsFactoryFactory(RegisterableItemsFactoryFactory registerableItemsFactoryFactory) {
        this.registerableItemsFactoryFactory = registerableItemsFactoryFactory;
    }
}
