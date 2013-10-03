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

package com.iterranux.droolsjbpmCore.internal;

import com.iterranux.droolsjbpmCore.runtime.build.impl.KieModuleBuilder;
import grails.util.GrailsNameUtils;
import grails.util.Metadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;

public class DroolsjbpmCoreUtils {

    private static final Log log = LogFactory.getLog(DroolsjbpmCoreUtils.class);

    GrailsPluginManager pluginManager;

    KieServices kieServices;

    public String getApplicationNameAsClassName(){

        return GrailsNameUtils.getNameFromScript(Metadata.getCurrent().getApplicationName());
    }

    public String getApplicationNameAsPropertyName(){

        return GrailsNameUtils.getPropertyName(getApplicationNameAsClassName());
    }

    public String getKmoduleGroupId(){
        return KieModuleBuilder.KMODULE_GROUP_ID;
    }

    /**
     * Determines the version of the named grails module.
     *
     * @param moduleName grails module - either a plugin or an app
     * @return Version of the grails module
     */
    public String getVersionForGrailsModule(String moduleName){

        if(pluginManager.hasGrailsPlugin(moduleName)){
            return pluginManager.getGrailsPlugin(moduleName).getVersion();
        }else if (getApplicationNameAsPropertyName().equals(moduleName)){
            return Metadata.getCurrent().getApplicationVersion();
        }
        log.error("ERROR: Could not determine version for grails module '"+ moduleName +"' because there is no grails module (plugin or application) with that name installed.");
        return null;
    }

    public ReleaseId getReleaseIdForGrailsModule(String moduleName){
        return kieServices.newReleaseId(getKmoduleGroupId(), moduleName, getVersionForGrailsModule(moduleName));
    }

    public void setPluginManager(GrailsPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }
}
