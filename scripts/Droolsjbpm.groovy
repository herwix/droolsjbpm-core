includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsArgParsing")

USAGE = """
    droolsjbpm [--init-kmodule]

where
    init-kmodule  = Initializes the folder structure to automatically build a kmodule.
                    At the moment this kmodule is only compatible with a grails-app
                    that has the droolsjbpm-core plugin installed.

"""

def kmoduleContent = """<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">

</kmodule>"""

target(droolsjbpm: "The droolsjbpm-core script.") {
    depends parseArguments
    try{
        if (argsMap["init-kmodule"]) {

            //Create folder structure
            def applicationUtils = classLoader.loadClass('com.iterranux.droolsjbpmCore.internal.ApplicationUtils')
            String folderName = applicationUtils.getApplicationNameAsPropertyName()
            ant.mkdir(dir:"${basedir}/grails-app/conf/droolsjbpm/resources/${folderName}")

            //Write kmodule.xml
            def kmodule = new File("${basedir}/grails-app/conf/droolsjbpm/resources/kmodule.xml")
            if(! kmodule.exists()){
                kmodule.write(kmoduleContent,'UTF-8')
            }

            println """
##### kmodule folder structure initialized! #####

You now have a folder 'grails-app/conf/droolsjbpm/resources/$folderName' where you can put your drools/jbpm resources.
This folder is treated like the 'src/main/resources' folder in a standard maven based kmodule. Just put your packages in there.

This also created a kmodule.xml file at 'grails-app/conf/droolsjbpm/resources/kmodule.xml'.
This can be used just as a regular kmodule.xml, however, it should not be moved to any other directory.

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
