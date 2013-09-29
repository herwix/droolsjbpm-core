package com.iterranux.droolsjbpmCore.internal;

import grails.util.GrailsNameUtils;
import grails.util.Metadata;

public class ApplicationUtils {

    public static String getApplicationNameAsClassName(){

        return GrailsNameUtils.getNameFromScript(Metadata.getCurrent().getApplicationName());
    }

    public static String getApplicationNameAsPropertyName(){

        return GrailsNameUtils.getPropertyName(getApplicationNameAsClassName());
    }
}
