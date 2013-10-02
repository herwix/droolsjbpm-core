package com.iterranux.droolsjbpmCore.runtime.build.impl;

import com.iterranux.droolsjbpmCore.internal.DroolsjbpmCoreUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

    private DroolsjbpmCoreUtils droolsjbpmCoreUtils;

    private Boolean reloadActive;

    public KieModuleBuilder(KieServices ks){
        kieServices = ks;
        kieResources = kieServices.getResources();
    }

    /**
     * Builds all kmodules from grails plugins or the kmodule from the parent application that it can find on the classpath.
     * Kmodules are registered under the release id:
     *
     *      org.grails.plugins : Plugin Name : Plugin Version
     *
     */
     @PostConstruct
     public void buildKmodulesForGrailsModules() throws IOException{

        log.debug("Starting build of KieModules for grails modules");

        //Find all kmodule.xml
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String pattern= "/droolsjbpm/*/kmodule.xml";
        org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:"+pattern);

        for(org.springframework.core.io.Resource kmoduleXml : resources){

            if(! isTestResource(kmoduleXml.getURL())){

                String moduleName = extractModuleNameFromKmoduleXmlUrl(kmoduleXml.getURL().toString());
                buildKmoduleForGrailsModule(moduleName, kmoduleXml);

            }

        }
        log.debug("Finished build of KieModules for grails modules");
    }

    public void buildKmoduleForGrailsModule(String moduleName, Resource kmoduleXml) throws IOException{

        //ReleaseId for this kmodule
        ReleaseId releaseId = droolsjbpmCoreUtils.getReleaseIdForGrailsModule(moduleName);

        //Only build kmodule for modules that exist
        if ( releaseId != null ){
            log.debug("Found KieModule for: "+moduleName);

            org.springframework.core.io.Resource[] moduleResources = getGrailsModuleResources(moduleName);

            KieFileSystem kfs = kieServices.newKieFileSystem();

            for(org.springframework.core.io.Resource moduleResource : moduleResources){
                URL moduleResourceURL = moduleResource.getURL();

                if (! isTestResource(moduleResourceURL)){

                    if(log.isDebugEnabled())
                        log.debug("Resources found for '"+moduleName+"': "+moduleResourceURL);

                    org.kie.api.io.Resource kieResource = kieResources.newUrlResource(moduleResourceURL);
                    kfs.write(getKmodulePathForResourceUrlAndModuleName(moduleResourceURL.toString(), moduleName), kieResource);

                }
            }

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
            log.error("Error: There was an attempt to build a KieModule for a plugin or application named '"+moduleName+"' which doesn't exist.\n" +
                    "       Please check that your KieModule directory structure is setup as intended:\n\n" +
                    "          1) Save any resources you might have in the 'grails-app/conf/droolsjbpm' to a secure location\n" +
                    "          2) Delete anything in the 'grails-app/conf/droolsjbpm' folder except the 'data' directory\n" +
                    "          3) Use the grails command 'droolsjbpm --init-kmodule' to set up a correct folder structure\n\n" +
                    "       If the error still persists please file a bug report.");
        }
    }

    public void buildKmoduleForGrailsModule(String moduleName){

        Resource kmoduleXml = new FileSystemResource("grails-app/conf/droolsjbpm/"+moduleName+"/kmodule.xml");
        if(kmoduleXml.exists()){
            try {
                buildKmoduleForGrailsModule(moduleName, kmoduleXml);
            }catch (IOException e){
                log.error("Error: KieModule build for '"+moduleName+"' failed", e);
            }
        }else{
            log.error("Error: KieModule build for '"+moduleName+"' was tried, but no kmodule.xml could be found.");
        }

    }

    protected Resource[] getGrailsModuleResources(String moduleName) throws IOException{

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        org.springframework.core.io.Resource[] moduleResources;

        if(moduleName.equals(droolsjbpmCoreUtils.getApplicationNameAsPropertyName()) && reloadActive){
            moduleResources = resolver.getResources("file:grails-app/conf/droolsjbpm/"+moduleName+"/resources/**");
        }else{
            moduleResources = resolver.getResources("classpath*:/droolsjbpm/"+moduleName+"/resources/**");
        }

        return moduleResources;
    }

    /**
     * Simple Method to check if resource is duplicated in target/test-classes on classpath which happens during testing.
     * WARNING: This also excludes non test files if there path somehow contains '/target/test-classes/' which is unlikely.
     *
     * @param resourceURL
     * @return
     */
    protected Boolean isTestResource(URL resourceURL){
        return resourceURL.toString().contains("/target/test-classes/");
    }

    /**
     * Trims the URL to the moduleName.
     * Expected URL format: abc/xyz/droolsjbpm/moduleName/kmodule.xml
     *
     * @param url of the kmodule.xml file on the classpath
     * @return moduleName
     */
    protected String extractModuleNameFromKmoduleXmlUrl(String url){
        url = url.substring(0, url.length()-12);
        return url.substring(url.lastIndexOf("/")+1);
    }

    /**
     * Creates the correct path for the referenced resource inside the kmodule.
     *
     * @param url of the resource
     * @param moduleName the resource resides in
     * @return path inside the kmodule
     */
    protected String getKmodulePathForResourceUrlAndModuleName(String url, String moduleName){
        String pattern = "/droolsjbpm/"+ moduleName +"/resources/";
        int index = url.indexOf(pattern);
        if ( index == -1){
            throw new IllegalArgumentException("The provided url had an unexpected format: '/droolsjbpm/"+ moduleName +"/resources/' couldn't be identified");
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

    public void setDroolsjbpmCoreUtils(DroolsjbpmCoreUtils droolsjbpmCoreUtils) {
        this.droolsjbpmCoreUtils = droolsjbpmCoreUtils;
    }

    public void setReloadActive(Boolean reloadActive) {
        this.reloadActive = reloadActive;
    }
}

