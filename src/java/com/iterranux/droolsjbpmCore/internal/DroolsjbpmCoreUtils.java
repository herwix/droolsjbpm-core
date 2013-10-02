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
