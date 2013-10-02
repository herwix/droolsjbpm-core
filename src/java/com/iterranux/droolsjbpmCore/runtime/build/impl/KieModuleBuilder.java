package com.iterranux.droolsjbpmCore.runtime.build.impl;

import grails.util.Holders;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.plugins.GrailsPlugin;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

public class KieModuleBuilder {

    private static final Log log = LogFactory.getLog(KieModuleBuilder.class);

    public static final String KMODULE_ROOT_PATH = "src/main/resources/";

    public static final String KMODULE_GROUP_ID = "org.grails.plugins";

    private KieServices kieServices;

    private KieResources kieResources;

    public KieModuleBuilder(KieServices ks){
        kieServices = ks;
        kieResources = kieServices.getResources();
    }

    /**
     * Builds all kmodules from grails plugins that it can find on the classpath.
     * Kmodules are registered under the release id:
     *
     *      org.grails.plugins : Plugin Name : Plugin Version
     *
     */
     @PostConstruct
     public void buildKmodulesForGrailsPlugins() throws IOException{

        log.debug("Starting build of kmodules for grails plugins");

        GrailsPluginManager pluginManager = Holders.getPluginManager();

        //Find all plugins which registered
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String pattern= "/droolsjbpm/*/kmodule.xml";
        org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:"+pattern);

        for(org.springframework.core.io.Resource kmoduleXml : resources){

            if(! isTestResource(kmoduleXml.getURL())){

                String pluginName = extractPluginNameFromKmoduleXmlUrl(kmoduleXml.getURL().toString());

                if ( pluginManager.hasGrailsPlugin(pluginName) ){

                    GrailsPlugin plugin = pluginManager.getGrailsPlugin(pluginName);

                    org.springframework.core.io.Resource[] pluginResources = resolver.getResources("classpath*:/droolsjbpm/"+pluginName+"/resources/**");

                    KieFileSystem kfs = kieServices.newKieFileSystem();

                    for(org.springframework.core.io.Resource pluginResource : pluginResources){
                        URL pluginResourceURL = pluginResource.getURL();

                        if (! isTestResource(pluginResourceURL)){

                            if(log.isDebugEnabled())
                                log.debug("Resources found for '"+pluginName+"': "+pluginResourceURL);

                            org.kie.api.io.Resource kieResource = kieResources.newUrlResource(pluginResourceURL);
                            kfs.write(getKmodulePathForResourceUrlAndPluginName(pluginResourceURL.toString(), pluginName), kieResource);

                        }
                    }

                    //ReleaseId for this kmodule
                    ReleaseId releaseId = kieServices.newReleaseId(KMODULE_GROUP_ID, pluginName, plugin.getVersion());

                    //Generate Basic POM
                    kfs.generateAndWritePomXML(releaseId);

                    //Add Kmodule.xml
                    kfs.writeKModuleXML(IOUtils.toByteArray(kmoduleXml.getInputStream()));

                    //Builder for the KieModule from the kfs, also possible from folder
                    KieBuilder kbuilder = kieServices.newKieBuilder(kfs);

                    //Build KieModule and automatically deploy to kieRepo if successful
                    kbuilder.buildAll();

                    //Throw RuntimeException if build failed
                    if(kbuilder.getResults().hasMessages(Message.Level.ERROR)){
                        throw new RuntimeException("Error: \n" + kbuilder.getResults().toString());
                    }
                }else{
                    log.error("Error: There was an attempt to build a kmodule for a plugin named '"+pluginName+"' which doesn't exist.\n" +
                              "       Please check that your kmodule directory structure is setup as intended:\n\n" +
                              "          1) Save any resources you might have in the 'grails-app/conf/droolsjbpm' to a secure location\n" +
                              "          2) Delete anything in the 'grails-app/conf/droolsjbpm' folder except the 'data' directory\n" +
                              "          3) Use the grails command 'droolsjbpm --init-kmodule' to set up a correct folder structure\n\n" +
                              "       If the error still persists please file a bug report.");
                }
            }

        }
        log.debug("Finished build of kmodules for grails plugins");
    }

    /**
     * Simple Method to check if resource is duplicated in target/test-classes on classpath which happens during testing.
     * WARNING: This also excludes non test files if there path somehow contains '/target/test-classes/' which is unlikely.
     *
     * @param resourceURL
     * @return
     */
    protected static Boolean isTestResource(URL resourceURL){
        return resourceURL.toString().contains("/target/test-classes/");
    }

    /**
     * Trims the URL to the pluginName.
     * Expected URL format: abc/xyz/droolsjbpm/pluginName/kmodule.xml
     *
     * @param url of the kmodule.xml file on the classpath
     * @return pluginName
     */
    protected static String extractPluginNameFromKmoduleXmlUrl(String url){
        url = url.substring(0, url.length()-12);
        return url.substring(url.lastIndexOf("/")+1);
    }

    /**
     * Creates the correct path for the referenced resource inside the kmodule.
     *
     * @param url of the resource
     * @param pluginName the resource resides in
     * @return path inside the kmodule
     */
    protected static String getKmodulePathForResourceUrlAndPluginName(String url, String pluginName){
        String pattern = "/droolsjbpm/"+pluginName+"/resources/";
        int index = url.indexOf(pattern);
        if ( index == -1){
            throw new IllegalArgumentException("The provided url had an unexpected format: '/droolsjbpm/"+pluginName+"/resources/' couldn't be identified");
        }
        url = url.substring(index+pattern.length());
        return KMODULE_ROOT_PATH + url;
    }

    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }

    public void setKieResources(KieResources kieResources) {
        this.kieResources = kieResources;
    }

}

