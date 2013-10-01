package com.iterranux.droolsjbpmCore.internal;

/**
 * Simple interface that guarantees the presence of the getTypeName method
 * for abstract factory manufacturables.
 */
public interface GenericFactoryManufacturable {

    public String getManufacturableTypeName();
}
