import com.iterranux.droolsjbpmCore.api.RuntimeManagerType
import com.iterranux.droolsjbpmCore.runtime.manager.api.RuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.GenericRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerProcessInstanceRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerProcessInstanceRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerRequestRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.SingletonRuntimeManagerFactory
import org.drools.core.base.MapGlobalResolver
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

class DroolsjbpmCoreGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def dependsOn = ['platformCore' : '* > 1.0.RC5']

    // TODO Fill in these fields
    def title = "Droolsjbpm Core Plugin" // Headline display name of the plugin
    def author = "Alexander Herwix"
    def authorEmail = ""
    def description = '''\
Integrates the droolsjbpm project with Grails and works as the foundation for Droolsjbpm based Grails-plugins.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/droolsjbpm-core"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    /**
     *   Platform - Core enabled configuration API
     *   http://grailsrocks.github.io/grails-platform-core/guide/configuration.html
     */
    def doWithConfigOptions = {
        'hibernateNamingStrategy.table.prefix'(type: String, defaultValue: 'DROOLSJBPM_')
        'entityManagerFactory.jpaProperties'(type: Properties, defaultValue: jpaProperties)
        'entityManagerFactory.datasource.beanName'(type: String, defaultValue: 'dataSourceUnproxied')
        'runtimeManager.singleton.identifier.location'(type: String, defaultValue: null)
        'runtimeManager.defaultImplementation'(type:String, defaultValue: PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE)

    }

    def doWithConfig = { config ->

        //Merge packages list
        def beanPackagesList = ['com.iterranux.droolsjbpmCore']
        if(config.grails.spring.bean.packages){
            beanPackagesList.addAll(config.grails.spring.bean.packages)
        }

        application {
            //enable spring annotations for these packages
            grails.spring.bean.packages = beanPackagesList
        }
    }

    def doWithSpring = {

        xmlns kie:"http://drools.org/schema/kie-spring"

        def pluginConfig = application.config.plugin.droolsjbpmCore

        droolsjbpmEntityManagerFactory(LocalContainerEntityManagerFactoryBean){
            jpaVendorAdapter = { HibernateJpaVendorAdapter a ->
            }

            packagesToScan = [
                    'org.drools.persistence',
                    'org.jbpm.persistence',
                    'org.jbpm.runtime.manager.impl.jpa',
                    'org.jbpm.services.task.impl.model',
                    'org.jbpm.process.audit'
            ]

            mappingResources = ['META-INF/JBPMorm.xml','META-INF/Taskorm.xml']
            persistenceUnitName = 'org.jbpm.persistence.jpa'

            jtaDataSource = ref(pluginConfig.entityManagerFactory.datasource.beanName)

            jpaProperties = pluginConfig.entityManagerFactory.jpaProperties
        }

        /**
         *  Need to set up beans manually because annotation driven config doesn't work
         *  http://jira.grails.org/browse/GRAILS-10365
         */
        runtimeManagerFactory(GenericRuntimeManagerFactory)
        //Create the factory beans to be used in the genericRuntimeManagerFactory
        singletonRuntimeManagerFactory(SingletonRuntimeManagerFactory)
        perProcessInstanceRuntimeManagerFactory(PerProcessInstanceRuntimeManagerFactory)
        perRequestRuntimeManagerFactory(PerRequestRuntimeManagerFactory)

        droolsjbpmGlobals(MapGlobalResolver)

        dacceptor(ClassObjectMarshallingStrategyAcceptor,['*.*'])

        kie.kstore(id:'kiestore')

        kie.environment(id: 'droolsjbpmEnvironment'){
            kie.'entity-manager-factory'(ref:'droolsjbpmEntityManagerFactory')

            kie.globals(ref:'droolsjbpmGlobals')

            kie.'object-marshalling-strategies'(){
                kie.'jpa-placeholder-resolver-strategy'()
                kie.'serializable-placeholder-resolver-strategy'('strategy-acceptor-ref':"dacceptor")
            }


        }

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->

        def pluginConfig = ctx.grailsApplication.config.plugin.droolsjbpmCore

        //TODO Can't access application in doWithConfigOptions
        System.setProperty('jbpm.data.dir', pluginConfig.runtimeManager.singleton.identifier.location ?: ctx.getResource('/droolsjbpm/data').file.toString())

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    /**
     * Default hibernate jpa properties for entityManagerFactory configured for H2 and Atomikos Transactions:
     *
     * hibernate.ejb.naming_strategy                : 'com.iterranux.droolsjbpmCore.persistence.DroolsjbpmNamingStrategy'
     * hibernate.max_fetch_depth                    : 3
     * hibernate.hbm2ddl.auto                       : 'update'
     * hibernate.show_sql                           : false
     * hibernate.dialect                            : 'org.hibernate.dialect.H2Dialect'
     * hibernate.transaction.manager_lookup_class   : 'com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup'
     *
     * @return jpaProperties
     */
    private static Properties getJpaProperties(){

        //hibernate properties from persistence.xml
        Properties props = new Properties()
        props.put('hibernate.ejb.naming_strategy','com.iterranux.droolsjbpmCore.persistence.DroolsjbpmNamingStrategy')
        props.put('hibernate.max_fetch_depth',3)
        props.put('hibernate.hbm2ddl.auto','update')
        props.put('hibernate.show_sql',false)
        props.put('hibernate.dialect','org.hibernate.dialect.H2Dialect')
        //jta manager lookup
        props.put('hibernate.transaction.manager_lookup_class', 'com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup')

        return props
    }
}
