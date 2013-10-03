/*
 * Copyright (c) 2013. Alexander Herwix
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * You can also obtain a commercial license. Contact: alex@herwix.com for further details.
 */

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
