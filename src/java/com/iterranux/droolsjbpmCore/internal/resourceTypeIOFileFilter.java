package com.iterranux.droolsjbpmCore.internal;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.kie.api.io.ResourceType;

import java.io.File;

/**
 * Simple FileFilter to determine if File is a ResourceType
 */
public class ResourceTypeIOFileFilter implements IOFileFilter{

    @Override
    public boolean accept(File file) {

        return ResourceType.determineResourceType(file.getName()) != null;
    }

    @Override
    public boolean accept(File dir, String name) {

        return ResourceType.determineResourceType(name) != null;
    }
}
