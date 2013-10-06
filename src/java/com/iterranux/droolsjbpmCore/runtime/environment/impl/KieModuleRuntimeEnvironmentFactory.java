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
