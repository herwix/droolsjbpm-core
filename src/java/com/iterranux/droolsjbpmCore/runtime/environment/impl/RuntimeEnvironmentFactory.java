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
 * Factory that allows for the easy creation of RuntimeEnvironments.
 * EntityManagerFactory and UserGroupCallback are (automatically) configured through spring.
 *
 */
public class RuntimeEnvironmentFactory {

    private static final Log log = LogFactory.getLog(RuntimeEnvironmentFactory.class);

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

        return RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(entityManagerFactory);

    }

    /**
     * RuntimeEnvironment factory method that automatically registers all drools resources in the given
     * directory into the RuntimeEnvironment KieBase.
     *
     * @param pathToLocalResourcesDir
     * @return RuntimeEnvironment with local resources in KieBase.
     */
    public RuntimeEnvironment newLocalResourcesRuntimeEnvironment(String pathToLocalResourcesDir){

        RuntimeEnvironmentBuilder builder = newDefaultRuntimeEnvironmentBuilder();

        if(pathToLocalResourcesDir == null){

            log.error("No path to the local resources folder was set. Please set a valid path for the config option or don't instantiate a LocalResourcesRuntimeEnvironment.");

        }else{
            //Folder in file system: Add all assets in droolsjbpm resources folder to kbase

            File resourcesFolder = new File(pathToLocalResourcesDir);

            if(resourcesFolder.isDirectory()){
                for(File file : FileUtils.listFiles(resourcesFolder, new ResourceTypeIOFileFilter(), TrueFileFilter.INSTANCE)){
                    builder.addAsset(ResourceFactory.newFileResource(file), ResourceType.determineResourceType(file.getName()));
                    log.debug("Added resource ("+file.getName()+") to the localResourcesRuntimeEnvironment.");
                }

            }else{
                log.error("The path ("+ pathToLocalResourcesDir +") to the local resources folder does not exist. Please set a valid path for the config option or don't instantiate a LocalResourcesRuntimeEnvironment.");
            }
        }

        return builder.get();

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
