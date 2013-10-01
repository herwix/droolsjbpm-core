package com.iterranux.droolsjbpmCore.runtime.build.impl

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class KieModuleBuilderSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test extraction of plugin name from kmodule.xml url"() {

        when:
            def pluginName = KieModuleBuilder.extractPluginNameFromKmoduleXmlUrl("abc/sdc/droolsjbpm/pluginName/kmodule.xml")

        then:
            pluginName == "pluginName"

    }

    void "test creation of internal kmodule path for resource path and pluginName"() {

        expect:
            path == KieModuleBuilder.getKmodulePathForResourceUrlAndPluginName(orgPath, "pluginName")


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
            KieModuleBuilder.getKmodulePathForResourceUrlAndPluginName(a, "pluginName")

        then:
            thrown(IllegalArgumentException)


        where:
        a << ["olsjbpm/pluginName/resources/com/iterranux/test/A",
              "droolsjbpm/pluginName/resources/A",
              "xyz/droolsjbpm/pluginName2/resources/a/b/C"]
    }
}
