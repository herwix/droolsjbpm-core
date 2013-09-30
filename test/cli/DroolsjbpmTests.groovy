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

        def kmodule = new File("grails-app/conf/droolsjbpm/droolsjbpmCore/kmodule.xml")
        def resources =  new File("grails-app/conf/droolsjbpm/droolsjbpmCore/resources")

        assertFalse("Kmodule resources Folder doesn't exist",resources.exists())
        assertFalse("Kmodule doesn't exists", kmodule.exists())

        execute(["droolsjbpm","--init-kmodule"])

        assertEquals 0, waitForProcess()
        verifyHeader()

        assertTrue("Kmodule resources folder exists.",resources.exists())
        assertTrue("Kmodule exists", kmodule.exists())
        assertTrue("Kmodule has content", kmodule.getText('UTF-8') != '')

    }

    void testInitKmoduleNoOverwrite() {

        cleanUp()

        def kmodule = new File("grails-app/conf/droolsjbpm/droolsjbpmCore/kmodule.xml")
        def resources =  new File("grails-app/conf/droolsjbpm/droolsjbpmCore/resources")

        assertFalse("Kmodule resources Folder doesn't exist",resources.exists())
        assertFalse("Kmodule doesn't exists", kmodule.exists())

        //Set up fake kmodule
        def folder = new File("grails-app/conf/droolsjbpm/droolsjbpmCore")
        folder.mkdirs()
        kmodule.write("TEST")


        execute(["droolsjbpm","--init-kmodule"])

        assertEquals 0, waitForProcess()
        verifyHeader()

        assertTrue("Kmodule resources folder exists.",resources.exists())
        assertTrue("Kmodule exists", kmodule.exists())
        assertTrue("Kmodule didn't change", kmodule.getText('UTF-8') == "TEST" )

    }
}
