package com.iterranux.droolsjbpmCore.runtime.build.impl;


import org.kie.api.KieServices;

public class KieModuleBuilder {

    KieServices kieServices;

    public boolean buildKieModule(String pattern, String releaseId){



        return true;
    }


    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }
}
