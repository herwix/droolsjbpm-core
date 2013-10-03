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

package com.iterranux.droolsjbpmCore.persistence;

import grails.util.Holders;
import org.hibernate.cfg.ImprovedNamingStrategy;

public class DroolsjbpmNamingStrategy extends ImprovedNamingStrategy {

    private static final String PREFIX = Holders.getConfig().flatten().get("plugin.droolsjbpmCore.hibernateNamingStrategy.table.prefix").toString();

    public String tableName(String tableName) {
        return addPrefix(super.tableName(tableName));
    }

    public String classToTableName(String className) {
        return addPrefix(super.classToTableName(className));
    }

    public String collectionTableName(final String ownerEntity,
                                      final String ownerEntityTable,
                                      final String associatedEntity,
                                      final String associatedEntityTable,
                                      final String propertyName) {
        return addPrefix(super.collectionTableName(ownerEntity,
                ownerEntityTable, associatedEntity, associatedEntityTable,
                propertyName));
    }

    public String logicalCollectionTableName(final String tableName,
                                             final String ownerEntityTable,
                                             final String associatedEntityTable,
                                             final String propertyName) {
        return addPrefix(super.logicalCollectionTableName(tableName,
                ownerEntityTable, associatedEntityTable, propertyName));
    }

    private static String addPrefix(final String composedTableName) {
        return PREFIX + composedTableName.toUpperCase();

    }
}
