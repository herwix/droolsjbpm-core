package com.iterranux.droolsjbpmCore.runtime.environment.impl;

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

    public RuntimeEnvironment newRuntimeEnvironment(String groupId, String artifactId, String version){

        return newRuntimeEnvironment(groupId, artifactId, version, null);
    }

    public RuntimeEnvironment newRuntimeEnvironment(String groupId, String artifactId, String version, String kbaseName){

        //Retrieve kcontainer by releaseID
        ReleaseId releaseId = kieServices.newReleaseId(groupId,artifactId,version);

        return newRuntimeEnvironment(releaseId, kbaseName);
    }

    public RuntimeEnvironment newRuntimeEnvironment(ReleaseId releaseId){

        return newRuntimeEnvironment(releaseId, null);
    }

    public RuntimeEnvironment newRuntimeEnvironment(ReleaseId releaseId, String kbaseName){

        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        KieBase kieBase;

        //Retrieve kbase by name (if null or empty use default)
        if( kbaseName == null || kbaseName == ""){
            kieBase = kieContainer.getKieBase();
        }else{
            kieBase = kieContainer.getKieBase(kbaseName);
        }

        RuntimeEnvironmentBuilder builder = newDefaultRuntimeEnvironmentBuilder();

        builder.knowledgeBase(kieBase);

        return builder.get();
    }

    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }

}
