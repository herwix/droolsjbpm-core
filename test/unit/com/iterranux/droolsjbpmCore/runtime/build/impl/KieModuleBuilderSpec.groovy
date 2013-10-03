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

package com.iterranux.droolsjbpmCore.runtime.build.impl

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.drools.compiler.kie.builder.impl.KieServicesImpl
import org.kie.api.KieServices
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class KieModuleBuilderSpec extends Specification {

    def kieModuleBuilder

    def setup() {

        def kieServices = mockFor(KieServicesImpl, false).createMock()

        kieModuleBuilder = new KieModuleBuilder(kieServices)
    }

    def cleanup() {
    }

    void "test extraction of plugin name from kmodule.xml url"() {

        when:
            def pluginName = kieModuleBuilder.extractModuleNameFromKmoduleXmlUrl("abc/sdc/droolsjbpm/pluginName/kmodule.xml")

        then:
            pluginName == "pluginName"

    }

    void "test creation of internal kmodule path for resource path and pluginName"() {

        expect:
            path == kieModuleBuilder.getKmodulePathForResourceUrlAndModuleName(orgPath, "pluginName")


        where:
            path    << ["src/main/resources/com/iterranux/test/A",
                        "src/main/resources/A",
                        "src/main/resources/a/b/C"]
            orgPath << ["abc/sdc/droolsjbpm/pluginName/resources/com/iterranux/test/A",
                        "/droolsjbpm/pluginName/resources/A",
                        "xyz/droolsjbpm/pluginName/resources/a/b/C"]
    }

    void "test creation of internal kmodule path for resource path and pluginName throws exception on illegal format"() {

        when:
            kieModuleBuilder.getKmodulePathForResourceUrlAndModuleName(a, "pluginName")

        then:
            thrown(IllegalArgumentException)


        where:
        a << ["olsjbpm/pluginName/resources/com/iterranux/test/A",
              "droolsjbpm/pluginName/resources/A",
              "xyz/droolsjbpm/pluginName2/resources/a/b/C"]
    }
}
