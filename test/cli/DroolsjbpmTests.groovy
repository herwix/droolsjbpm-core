import grails.test.AbstractCliTestCase

class DroolsjbpmTests extends AbstractCliTestCase {

    protected void setUp() {
        super.setUp()
        def folder = new File("grails-app/conf/droolsjbpm")
        if(folder.exists()){
            folder.renameTo(new File("grails-app/conf/droolsjbpm_SAVE"))
        }
    }

    protected void tearDown() {
        super.tearDown()

        cleanUp()

        def folder = new File("grails-app/conf/droolsjbpm_SAVE")
        if(folder.exists()){
            folder.renameTo(new File("grails-app/conf/droolsjbpm"))
        }
    }

    protected void cleanUp(){
        def folder = new File("grails-app/conf/droolsjbpm")

        if(folder.exists()){
            folder.deleteDir()
        }
    }

    void testDroolsjbpm() {

        execute(["droolsjbpm"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }

    void testInitKmodule() {

        cleanUp()

        def kmoduleXml = new File("grails-app/conf/droolsjbpm/droolsjbpmCore/kmodule.xml")
        def resources =  new File("grails-app/conf/droolsjbpm/droolsjbpmCore/resources")

        assertFalse("Kmodule resources Folder doesn't exist",resources.exists())
        assertFalse("kmodule.xml doesn't exists", kmoduleXml.exists())

        execute(["droolsjbpm","--init-kmodule"])

        assertEquals 0, waitForProcess()
        verifyHeader()

        assertTrue("Kmodule resources folder exists.",resources.exists())
        assertTrue("kmodule.xml exists", kmoduleXml.exists())
        assertTrue("kmodule.xml has content", kmoduleXml.getText('UTF-8') != '')

    }

    void testInitKmoduleNoOverwrite() {

        cleanUp()

        def kmoduleXml = new File("grails-app/conf/droolsjbpm/droolsjbpmCore/kmodule.xml")
        def resources =  new File("grails-app/conf/droolsjbpm/droolsjbpmCore/resources")

        assertFalse("Kmodule resources Folder doesn't exist",resources.exists())
        assertFalse("kmodule.xml doesn't exists", kmoduleXml.exists())

        //Set up fake kmodule
        def folder = new File("grails-app/conf/droolsjbpm/droolsjbpmCore")
        folder.mkdirs()
        def kmoduleContent = """<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://jboss.org/kie/6.0.0/kmodule">

    <kbase name="Test" packages="*">
        <ksession name="OnlyTest" />
    </kbase>

</kmodule>
"""
        kmoduleXml.write(kmoduleContent,"UTF-8")


        assertTrue("kmodule.xml exists", kmoduleXml.exists())

        execute(["droolsjbpm","--init-kmodule"])

        assertEquals 0, waitForProcess()
        verifyHeader()

        assertTrue("Kmodule resources folder exists.",resources.exists())
        assertTrue("kmodule.xml exists", kmoduleXml.exists())
        assertTrue("kmodule.xml didn't change", kmoduleXml.getText('UTF-8') == kmoduleContent )

    }
}
