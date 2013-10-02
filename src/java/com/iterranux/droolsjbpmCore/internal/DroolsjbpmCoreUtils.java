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

    public String getVersionForPlugin(String pluginName){

        if(pluginManager.hasGrailsPlugin(pluginName)){
            return pluginManager.getGrailsPlugin(pluginName).getVersion();
        }
        log.error("ERROR: Could not determine version for plugin '"+pluginName+"' because there is no plugin with that name installed.");
        return null;
    }

    public ReleaseId getReleaseIdForPlugin(String pluginName){
        return kieServices.newReleaseId(getKmoduleGroupId(),pluginName,getVersionForPlugin(pluginName));
    }

    public void setPluginManager(GrailsPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }
}
