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

import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.manager.RuntimeEnvironment;

/**
 * A factory that builds a runtimeEnvironment based upon a kmodule.
 *
 */
public class KieModuleRuntimeEnvironmentFactory extends AbstractRuntimeEnvironmentFactory{

    KieServices kieServices;

    protected AuditEventBuilder auditEventBuilder = new DefaultAuditEventBuilderImpl();

    /**
     * Add the kieBase identified by releaseId and kieBaseName to the RuntimeEnvironmentBuilder.
     *
     * @param releaseId of the kieModule the kieBase is in
     * @param kieBaseName of the desired kieBase - use null for default
     * @param kieSessionName of the desired KieSession - use null for default
     * @return RuntimeEnvironmentBuilder with configured KieBase
     */
    public RuntimeEnvironmentBuilder newRuntimeEnvironmentBuilder(ReleaseId releaseId, String kieBaseName, String kieSessionName){

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        KieBase kieBase;

        //Retrieve kbase by name (if null or empty use default)
        if( kieBaseName == null || kieBaseName.equals("")){
            kieBase = kieContainer.getKieBase();
        }else{
            kieBase = kieContainer.getKieBase(kieBaseName);
        }

        RuntimeEnvironmentBuilder builder = newDefaultRuntimeEnvironmentBuilder();

        builder.knowledgeBase(kieBase);

        //Add KModuleRegisterableItemsFactory to register Listeners, WorkItemHandlers registered in kmodule.xml as default
        if(auditEventBuilder == null){
            builder.registerableItemsFactory(registerableItemsFactoryFactory.newDroolsjbpmCoreKModuleRegisterableItemFactory(kieContainer, kieSessionName));
        }else{
            builder.registerableItemsFactory(registerableItemsFactoryFactory.newDroolsjbpmCoreKModuleRegisterableItemFactory(kieContainer, kieSessionName, auditEventBuilder));
        }

        return builder;
    }

    /**
     * Convenience method to get runtimeEnvironment for params.
     * @param groupId of kieModule
     * @param artifactId of kieModule
     * @param version of kieModule
     * @return runtimeEnvironment for default kieBase of kieModule
     */
    public RuntimeEnvironment newRuntimeEnvironment(String groupId, String artifactId, String version){

        return newRuntimeEnvironment(groupId, artifactId, version, null, null);
    }

    /**
     * Convenience method to get runtimeEnvironment for params.
     * @param groupId of kieModule
     * @param artifactId of kieModule
     * @param version of kieModule
     * @param kieBaseName of of the desired kieBase - use null for default
     * @param kieSessionName of the desired KieSession - use null for default
     * @return runtimeEnvironment for kieBase with kieBaseName of kieModule
     */
    public RuntimeEnvironment newRuntimeEnvironment(String groupId, String artifactId, String version, String kieBaseName, String kieSessionName){

        ReleaseId releaseId = kieServices.newReleaseId(groupId,artifactId,version);

        return newRuntimeEnvironment(releaseId, kieBaseName, kieSessionName);
    }

    /**
     * Convenience method to get runtimeEnvironment for params.
     * @param releaseId of kieModule
     * @return runtimeEnvironment for default kieBase of kieModule
     */
    public RuntimeEnvironment newRuntimeEnvironment(ReleaseId releaseId){

        return newRuntimeEnvironment(releaseId, null, null);
    }

    /**
     * Convenience method to get runtimeEnvironment for params.
     * @param releaseId of kieModule
     * @param kieBaseName of the desired kieBase - use null for default
     * @param kieSessionName of the desired KieSession - use null for default
     * @return runtimeEnvironment for kieBase with kieBaseName of kieModule
     */
    public RuntimeEnvironment newRuntimeEnvironment(ReleaseId releaseId, String kieBaseName, String kieSessionName){

        RuntimeEnvironmentBuilder builder = newRuntimeEnvironmentBuilder(releaseId, kieBaseName, kieSessionName);

        return builder.get();
    }

    /**
     * Convenience method to get runtimeEnvironmentBuilder with the kieBase added.
     * @param groupId of kieModule
     * @param artifactId of kieModule
     * @param version of kieModule
     * @return runtimeEnvironmentBuilder with default kieBase from kieModule
     */
    public RuntimeEnvironmentBuilder newRuntimeEnvironmentBuilder(String groupId, String artifactId, String version){

        return newRuntimeEnvironmentBuilder(groupId, artifactId, version, null, null);
    }

    /**
     * Convenience method to get runtimeEnvironmentBuilder with the kieBase added.
     * @param groupId of kieModule
     * @param artifactId of kieModule
     * @param version of kieModule
     * @param kieBaseName of the desired kieBase - use null for default
     * @param kieSessionName of the desired KieSession - use null for default
     * @return runtimeEnvironmentBuilder with kieBase from kieBaseName of kieModule
     */
    public RuntimeEnvironmentBuilder newRuntimeEnvironmentBuilder(String groupId, String artifactId, String version, String kieBaseName, String kieSessionName){

        ReleaseId releaseId = kieServices.newReleaseId(groupId,artifactId,version);

        return newRuntimeEnvironmentBuilder(releaseId, kieBaseName, kieSessionName);
    }

    /**
     * Convenience method to get runtimeEnvironmentBuilder with the kieBase added.
     * @param releaseId of kieModule
     * @return runtimeEnvironmentBuilder with default kieBase from kieModule
     */
    public RuntimeEnvironmentBuilder newRuntimeEnvironmentBuilder(ReleaseId releaseId){

        return newRuntimeEnvironmentBuilder(releaseId, null, null);
    }

    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }


    public void setAuditEventBuilder(AuditEventBuilder auditEventBuilder) {
        this.auditEventBuilder = auditEventBuilder;
    }

}
