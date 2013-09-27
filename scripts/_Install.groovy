//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//

//create standard droolsjbpm folder structure
ant.mkdir(dir:"${basedir}/grails-app/conf/droolsjbpm/resources")
ant.mkdir(dir:"${basedir}/grails-app/conf/droolsjbpm/data")

