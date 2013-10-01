includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsArgParsing")
includeTargets << grailsScript("_GrailsPackage")
includeTargets << grailsScript('_GrailsBootstrap')

USAGE = """
    droolsjbpm [--init-kmodule] [--init-local-resources]

where
    init-kmodule  = Initializes the folder structure to automatically build a kmodule.
                    At the moment this kmodule is only compatible with a grails-app
                    that has the droolsjbpm-core plugin installed.

    init-local-resources = Initilizes the folder structure for a simple local resources
                           based runtimeEnvironment.

"""

def kmoduleContent = """<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">

</kmodule>"""

target(droolsjbpm: "The droolsjbpm-core script.") {
    depends parseArguments, compile, loadApp, configureApp
    try{
        if (argsMap["init-kmodule"]) {

            //Create folder structure
            def applicationUtils = classLoader.loadClass('com.iterranux.droolsjbpmCore.internal.ApplicationUtils')
            String folderName = applicationUtils.getApplicationNameAsPropertyName()
            ant.mkdir(dir:"${basedir}/grails-app/conf/droolsjbpm/${folderName}/resources")

            //Write kmodule.xml
            def kmodule = new File("${basedir}/grails-app/conf/droolsjbpm/${folderName}/kmodule.xml")
            if(! kmodule.exists()){
                kmodule.write(kmoduleContent,'UTF-8')
            }

            println """
##### kmodule folder structure initialized! #####

You now have a folder 'grails-app/conf/droolsjbpm/$folderName/resources' where you can put your drools/jbpm resources.
This folder is treated like the 'src/main/resources' folder in a standard maven based kmodule. Just put your packages in there.

This also created a kmodule.xml file at 'grails-app/conf/droolsjbpm/$folderName/kmodule.xml'.
This can be used just as a regular kmodule.xml, however, it should not be moved to any other directory.

Happy Coding!       """

        }else if (argsMap["init-local-resources"]) {
            String location = grailsApp.config.plugin.droolsjbpmCore.runtimeManager.localResources.dir
            def folder = new File(location)
            if (!folder.exists()){
                folder.mkdirs()
            }
            println """
##### local resources folder structure initialized! #####

You now have a folder at '${folder.toPath()}' where you can put your drools/jbpm resources.
If you now set the 'plugin.droolsjbpmCore.runtimeManager.localResources.activate' option to true,
all droolsjbpm resources in there will be added to a set of 3 different RuntimeManagers that can be
used by your application via injection (e.g. use them like a grails service).

They are named:

    singletonRuntimeManager - a global session for all resources

    perProcessInstanceRuntimeManager - a new session for every process, kept until process is finished

    perRequestRuntimeManager - a new session for every request

For more information about runtimeManager please refer to the jbpm 6 documentation.

Happy Coding!       """
        }else{
            println "You have to specify an option for this plugin: \n"
            println USAGE
        }
    }catch (ClassNotFoundException e){
        println "Ooops, there was a ClassNotFoundException during the execution of the script."
        println "Please compile the project and try again!"
    }
}

setDefaultTarget(droolsjbpm)
