package com.iterranux.droolsjbpmCore.runtime.impl;


import com.iterranux.droolsjbpmCore.runtime.api.RegisterableRuntimeManager;
import com.iterranux.droolsjbpmCore.utils.AbstractFactory;
import grails.util.Holders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class RegisterableRuntimeManagerFactory extends AbstractFactory<RegisterableRuntimeManager> {

    public RegisterableRuntimeManagerFactory() {
        super();
    }

    /**
     * Implements the abstract method getManufacturableClassNames() to
     * retrieve the collection of manufacturables class names from the plugin configuration.
     *
     * @return Collection<String> of manufacturables class names
     */
    @Override
    protected Collection<String> getManufacturableClassNames() {
        System.out.println(Holders.getFlatConfig().keySet());
        //return (Collection<String>) Holders.getFlatConfig().get("plugin.droolsjbpmCore.runtimeManager.implementations");
        return Arrays.asList("com.iterranux.droolsjbpmCore.runtime.impl.RegisterablePerProcessInstanceRuntimeManager");
    }
}
