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

import com.iterranux.droolsjbpmCore.internal.DroolsjbpmCoreUtils
import com.iterranux.droolsjbpmCore.runtime.build.impl.KieModuleBuilder
import com.iterranux.droolsjbpmCore.runtime.environment.impl.KieModuleRuntimeEnvironmentFactory
import com.iterranux.droolsjbpmCore.runtime.environment.impl.LocalResourcesRuntimeEnvironmentFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.GenericRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerProcessInstanceRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.PerRequestRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.runtime.manager.impl.SingletonRuntimeManagerFactory
import com.iterranux.droolsjbpmCore.task.impl.SpringTaskServiceFactory

import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl
import org.kie.api.KieServices
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

class DroolsjbpmCoreGrailsPlugin {

    private Logger log = LoggerFactory.getLogger('com.iterranux.droolsjbpmCore.DroolsjbpmCoreGrailsPlugin')

    // the plugin version
    def version = "1.0.RC1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/conf/droolsjbpm/**"
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

    def watchedResources = "file:./grails-app/conf/droolsjbpm/*/resources/**"

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

        //relative path resolves against project root as parent. Even for installed plugins.
        'path.to.jbpm.data.dir'(type: String, defaultValue: 'grails-app/conf/droolsjbpm/data')

        'runtimeManager.localResources.activate'(type:Boolean, defaultValue: false)
        'runtimeManager.localResources.dir'(type: String, defaultValue: 'src/resources')

        'runtimeManager.kieModule.reloadActive'(type:Boolean, defaultValue: true)

        'taskservice.userGroupCallback.disable'(type:Boolean, defaultValue: false)
        'taskservice.userGroupCallback.userProperties'(type:Properties, defaultValue: getUserProperties())

        'transactionManager.jndi.lookup'(type: String, defaultValue: 'java:comp/TransactionManager')
        'transactionManager.userTransaction.jndi.lookup'(type: String, defaultValue: 'java:comp/UserTransaction')
    }

    def doWithConfig = { config ->


        /*
        Activate Bean Package Annotations does not work, see at the bottom of doWithSpring
        //Merge packages list
        def beanPackagesList = ['com.iterranux.droolsjbpmCore']
        if(config.grails.spring.bean.packages){
            beanPackagesList.addAll(config.grails.spring.bean.packages)
        }

        application {
            //enable spring annotations for these packages
            grails.spring.bean.packages = beanPackagesList
        }
        */
    }

    def doWithSpring = {

        def pluginConfig = application.config.plugin.droolsjbpmCore

        /**
         * Set up jbpm data dir
         */
        System.setProperty('jbpm.data.dir', new File(pluginConfig.path.to.jbpm.data.dir.toString()).absolutePath)

        /**
         * Set up Transaction Manager
         */
        System.setProperty('jbpm.ut.jndi.lookup', pluginConfig.transactionManager.userTransaction.jndi.lookup.toString())
        System.setProperty('jbpm.tm.jndi.lookup', pluginConfig.transactionManager.jndi.lookup.toString())

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
        droolsjbpmRuntimeManagerFactory(GenericRuntimeManagerFactory){ bean ->

            taskServiceFactory = ref('droolsjbpmTaskServiceFactory')

            bean.lazyInit = true

            bean.dependsOn = 'droolsjbpmKieModuleBuilder'
        }

        /**
         * Set up an abstract bean that provides the properties for the AbstractRuntimeEnvironmentFactory.
         * Should be used as bean.parent by RuntimeEnvironmentFactory Implementations.
         */
        abstractRuntimeEnvironmentFactory {
            entityManagerFactory = ref('droolsjbpmEntityManagerFactory')
            userGroupCallback = ref('droolsjbpmUserGroupCallback')
        }

        /**
         * Set up a factory bean for runtimeEnvironments based on local resources
         */
        droolsjbpmLocalResourcesRuntimeEnvironmentFactory(LocalResourcesRuntimeEnvironmentFactory){ bean ->
            bean.parent = abstractRuntimeEnvironmentFactory
        }

        /**
         * Set up a factory bean for runtimeEnvironments based on kmodules.
         * This will mostly be used by plugins which are built upon this core plugin.
         */
        droolsjbpmKmoduleRuntimeEnvironmentFactory(KieModuleRuntimeEnvironmentFactory){ bean ->
            bean.parent = abstractRuntimeEnvironmentFactory
            kieServices = ref('kieServices')
        }

        /**
         * Set up local resources if configured to do so. This provides 3 basic runtimeManagers for the client application.
         * They are lazy init so they are only initialized if needed. This is intended to allow a quick start with the plugin.
         */
        if(pluginConfig.runtimeManager.localResources.activate){

            /**
             * Creates a RuntimeEnvironment with local resources in the directory provided by plugin.droolsjbpmCore.runtimeManager.localResources.dir.
             */
            droolsjbpmLocalResourcesRuntimeEnvironment(droolsjbpmLocalResourcesRuntimeEnvironmentFactory:'newRuntimeEnvironment',
                    pluginConfig.runtimeManager.localResources.dir)

            /**
             *  Per Process Instance
             */
            perProcessInstanceRuntimeManager(droolsjbpmRuntimeManagerFactory: 'newRuntimeManager',
                    PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE,
                    ref('droolsjbpmLocalResourcesRuntimeEnvironment'),
                    'droolsjbpmCore-'+PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE){ bean ->

                bean.lazyInit = true
                bean.destroyMethod = "close"
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
         * Create kieServices bean to make it available for injection
         */
        kieServices(KieServices.Factory){ bean ->
            bean.factoryMethod = 'get'
        }

        /**
         * Set up droolsjbpmCoreUtils helper bean
         */
        droolsjbpmCoreUtils(DroolsjbpmCoreUtils){ bean ->
            bean.lazyInit = true

            pluginManager = manager
            kieServices = ref('kieServices')
        }

        /**
         * Set up the KieModuleBuilder which automatically builds all grails plugin kmodules on the classpath.
         */
        droolsjbpmKieModuleBuilder(KieModuleBuilder,ref('kieServices')){
            droolsjbpmCoreUtils = ref('droolsjbpmCoreUtils')
            reloadActive = pluginConfig.runtimeManager.kieModule.reloadActive
        }

        /**
         * Example set up of a runtimeManager for a plugin
         * -----------------------------------------------
         *
         * 1) Create an injectible releaseId for this plugin
         *
         * pluginReleaseId(droolsjbpmCoreUtils:'getReleaseIdForGrailsModule','pluginName')
         *
         * 2) Create a RuntimeEnvironment for this plugin
         *
         * pluginRuntimeEnvironment(droolsjbpmKmoduleRuntimeEnvironmentFactory:'newRuntimeEnvironment',
         *                                  ref('pluginReleaseId'))
         *
         * 3) Per Process Instance RuntimeManager for this plugin
         *
         * pluginRuntimeManager(droolsjbpmRuntimeManagerFactory: 'newRuntimeManager',
         *                          PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE,
         *                          ref('pluginRuntimeEnvironment'),
         *                          'plugin-'+PerProcessInstanceRuntimeManagerFactory.RUNTIME_MANAGER_TYPE){ bean ->
         *
         *                              bean.lazyInit = true
         *                              bean.destroyMethod = "close"
         *                          }
         */

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->

    }

    def onChange = { event ->
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.

        if(event.ctx && event.application.config.plugin.droolsjbpmCore.runtimeManager.kieModule.reloadActive){

            log.debug(event.source.toString() +" changed and triggered a kieModule reload.")

            def moduleName = event.ctx.getBean('droolsjbpmCoreUtils').getApplicationNameAsPropertyName()
            //rebuild KieModule for module
            event.ctx.getBean('droolsjbpmKieModuleBuilder').buildKmoduleForGrailsModule(moduleName)
        }
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
