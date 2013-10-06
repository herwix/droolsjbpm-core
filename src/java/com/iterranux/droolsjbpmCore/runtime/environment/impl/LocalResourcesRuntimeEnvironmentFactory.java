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

import com.iterranux.droolsjbpmCore.internal.ResourceTypeIOFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;

import java.io.File;

/**
 * RuntimeEnvironment factory implementation that provides a runtimeEnvironment build around
 * a local resource dir. It's really simple and doesn't allow much configuration. Provide
 * an accessible directory and it packs all droolsjbpm resources inside it into a default
 * kbase. No further Session or Kbase options are possible.
 */
public class LocalResourcesRuntimeEnvironmentFactory extends AbstractRuntimeEnvironmentFactory{

    private static final Log log = LogFactory.getLog(LocalResourcesRuntimeEnvironmentFactory.class);

    /**
     * RuntimeEnvironment factory method that automatically registers all drools resources in the given
     * directory into the RuntimeEnvironment KieBase.
     *
     * @param pathToLocalResourcesDir either relative to the application root or absolute.
     * @return RuntimeEnvironment with local resources in default KieBase.
     */
    public RuntimeEnvironment newRuntimeEnvironment(String pathToLocalResourcesDir){

        RuntimeEnvironmentBuilder builder = newDefaultRuntimeEnvironmentBuilder();
        builder.registerableItemsFactory(registerableItemsFactoryFactory.newDroolsjbpmCoreDefaultRegisterableItemsFactory());

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
}
