import com.iterranux.droolsjbpmCore.internal.TransactionManagerJNDIRegistrator
import com.iterranux.droolsjbpmCore.runtime.manager.impl.RuntimeEnvironmentFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.GenericRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerProcessInstanceRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerRequestRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.SingletonRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.task.impl.SpringTaskServiceFactory
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter



class DroolsjbpmCoreGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "web-app/droolsjbpm/**"
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
        'entityManagerFactory.persistenceUnitName'(type: String, defaultValue: 'org.jbpm.persistence.jpa')
        'entityManagerFactory.packagesToScan'(type: List,
                defaultValue: [ 'org.drools.persistence',
                                'org.jbpm.persistence',
                                'org.jbpm.runtime.manager.impl.jpa',
                                'org.jbpm.services.task.impl.model',
                                'org.jbpm.process.audit' ])
        'entityManagerFactory.mappingResources'(type: List, defaultValue: [ 'META-INF/JBPMorm.xml','META-INF/Taskorm.xml' ])

        //default value = web-app/droolsjbpm/data - can't be set here no servletContex.
        'path.to.jbpm.data.dir'(type: String, defaultValue: null)
        'path.to.localResources.dir'(type: String, defaultValue: 'web-app/droolsjbpm/resources')

        'runtimeManager.default.registerWithSpring'(type:Boolean, defaultValue: true)

        'taskservice.userGroupCallback.disable'(type:Boolean, defaultValue: false)
        'taskservice.userGroupCallback.userProperties'(type:Properties, defaultValue: getUserProperties())

        'transactionManager.registerToJNDI'(type:Boolean, defaultValue: true)
        'transactionManager.beanName'(type: String, defaultValue: 'atomikosTransactionManager')
        'transactionManager.jndi.lookup'(type: String, defaultValue: 'java:comp/TransactionManager')
        'transactionManager.userTransaction.jndi.lookup'(type: String, defaultValue: 'java:comp/UserTransaction')
        'transactionManager.userTransaction.beanName'(type: String, defaultValue: 'atomikosUserTransaction')
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

        def pluginConfig = application.config.plugin.droolsjbpmCore

        /**
         * Set up Transaction Manager
         */
        System.setProperty('jbpm.ut.jndi.lookup', pluginConfig.transactionManager.userTransaction.jndi.lookup.toString())
        System.setProperty('jbpm.tm.jndi.lookup', pluginConfig.transactionManager.jndi.lookup.toString())

        if(pluginConfig.transactionManager.registerToJNDI){
            droolsjbpmTransactionManagerJNDIRegistrator(TransactionManagerJNDIRegistrator){

                transactionManager = ref(pluginConfig.transactionManager.beanName.toString())
                transactionManagerLookup = pluginConfig.transactionManager.jndi.lookup.toString()

                userTransaction = ref(pluginConfig.transactionManager.userTransaction.beanName.toString())
                userTransactionLookup = pluginConfig.transactionManager.userTransaction.jndi.lookup.toString()
            }
        }

        /**
         * Persistence via EntityManagerFactory
         */
        droolsjbpmEntityManagerFactory(LocalContainerEntityManagerFactoryBean){
            jpaVendorAdapter = ref('droolsjbpmJpaVendorAdapter')

            packagesToScan = pluginConfig.entityManagerFactory.packagesToScan

            mappingResources = pluginConfig.entityManagerFactory.mappingResources
            persistenceUnitName = pluginConfig.entityManagerFactory.persistenceUnitName

            jtaDataSource = ref(pluginConfig.entityManagerFactory.datasource.beanName.toString())

            jpaProperties = pluginConfig.entityManagerFactory.jpaProperties
        }
        droolsjbpmJpaVendorAdapter(HibernateJpaVendorAdapter)

        /**
         * Set up local task service factory (required to work with runtimeManager)
         */
        droolsjbpmTaskServiceFactory(SpringTaskServiceFactory){
            entityManagerFactory = ref('droolsjbpmEntityManagerFactory')
            userGroupCallback = ref('droolsjbpmUserGroupCallback')
        }


        if(! pluginConfig.taskservice.userGroupCallback.disable){
                /**
                 * Set up a simple UserGroupCallback for test purposes backed by a Properties obj with format:
                 * String Username : String Roles (comma seperated)
                 */
                droolsjbpmTestUserGroupCallback(JBossUserGroupCallbackImpl, (Properties) pluginConfig.taskservice.userGroupCallback.userProperties)
                springConfig.addAlias('droolsjbpmUserGroupCallback','droolsjbpmTestUserGroupCallback')
        }

        /**
         * Set up the generic runtimeManagerFactory
         */
        droolsjbpmRuntimeManagerFactory(GenericRuntimeManagerFactory){
            taskServiceFactory = ref('droolsjbpmTaskServiceFactory')
        }

        /**
         * Set up the default runtimeEnvironmentFactory
         */
        droolsjbpmRuntimeEnvironmentFactory(RuntimeEnvironmentFactory){
            entityManagerFactory = ref('droolsjbpmEntityManagerFactory')
            userGroupCallback = ref('droolsjbpmUserGroupCallback')
        }

        /**
         * Creates a RuntimeEnvironment with a kbase based on assets in the given directory.
         */
        droolsjbpmLocalResourcesRuntimeEnvironment(droolsjbpmRuntimeEnvironmentFactory: 'newLocalResourcesRuntimeEnvironment',
                pluginConfig.path.to.localResources.dir)

        /**
         * Set up default runtimeManagers. They are lazy init so they are only initialized if needed.
         */
        if(pluginConfig.runtimeManager.default.registerWithSpring){

            /**
             *  Per Process Instance
             */
            perProcessInstanceRuntimeManager(droolsjbpmRuntimeManagerFactory: 'newRuntimeManager',
                    PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE,
                    ref('droolsjbpmLocalResourcesRuntimeEnvironment'),
                    'droolsjbpmCore-'+PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE){ bean ->

                bean.lazyInit = true
                bean.destroyMethod = "close"

                //wait for JNDI registration if needed
                if(pluginConfig.transactionManager.registerToJNDI){
                    bean.dependsOn = 'droolsjbpmTransactionManagerJNDIRegistrator'
                }
            }
            /**
             *  Singleton
             */
            singletonRuntimeManager(droolsjbpmRuntimeManagerFactory: 'newRuntimeManager',
                    SingletonRuntimeManagerFactory.RUNTIME_MANAGER_TYPE,
                    ref('droolsjbpmLocalResourcesRuntimeEnvironment'),
                    'droolsjbpmCore-'+SingletonRuntimeManagerFactory.RUNTIME_MANAGER_TYPE){ bean ->

                bean.lazyInit = true
                bean.destroyMethod = "close"

                //wait for JNDI registration if needed
                if(pluginConfig.transactionManager.registerToJNDI){
                    bean.dependsOn = 'droolsjbpmTransactionManagerJNDIRegistrator'
                }
            }
            /**
             *  Per Request
             */
            perRequestRuntimeManager(droolsjbpmRuntimeManagerFactory: 'newRuntimeManager',
                    PerRequestRuntimeManagerFactory.RUNTIME_MANAGER_TYPE,
                    ref('droolsjbpmLocalResourcesRuntimeEnvironment'),
                    'droolsjbpmCore-'+PerRequestRuntimeManagerFactory.RUNTIME_MANAGER_TYPE){ bean ->

                bean.lazyInit = true
                bean.destroyMethod = "close"

                //wait for JNDI registration if needed
                if(pluginConfig.transactionManager.registerToJNDI){
                    bean.dependsOn = 'droolsjbpmTransactionManagerJNDIRegistrator'
                }
            }

        }

        /**
         *  Create the factory beans to be used in the genericRuntimeManagerFactory
         *
         *  Need to set up beans manually because annotation driven config doesn't work
         *  http://jira.grails.org/browse/GRAILS-10365
         *  TODO: Remove when bug is resolved
         */
        droolsjbpmSingletonRuntimeManagerFactory(SingletonRuntimeManagerFactory)
        droolsjbpmPerProcessInstanceRuntimeManagerFactory(PerProcessInstanceRuntimeManagerFactory)
        droolsjbpmPerRequestRuntimeManagerFactory(PerRequestRuntimeManagerFactory)


        /**
         * Drools Spring Integration, KStore and Environment set up.
         *

        xmlns kie:"http://drools.org/schema/kie-spring"

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

         */

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->

        def pluginConfig = ctx.grailsApplication.config.plugin.droolsjbpmCore
        System.setProperty('jbpm.data.dir', pluginConfig.jbpm.data.dir ?: ctx.getResource('/droolsjbpm/data').file.toString())

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

    /**
     * Simple test set of user properties.
     *
     * @return Properties (String username : String roles)
     */
    private static Properties getUserProperties(){

        Properties props = new Properties();
        props.put('krisv','admin,manager,user')
        props.put('john','admin,manager,user,PM')
        props.put('mary','admin,manager,user,HR')
        props.put('reviewer','user,PM')
        props.put('translator','user,PM')
        props.put('manager','manager,HR')
        props.put('salaboy','admin,user')
        props.put('Administrator','Administrators')

        return props;
    }
}
