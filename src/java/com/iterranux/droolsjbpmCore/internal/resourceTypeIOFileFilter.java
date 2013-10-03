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
