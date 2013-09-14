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
