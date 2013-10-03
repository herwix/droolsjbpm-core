droolsjbpm-core
===============

A plugin to integrate the Drools/jBPM V6 project with grails.


License 
=======
This plugin is licensed under an open-source (AGPLv3)/commerical dual-License.
See the license file for details.


Installation
===============

The simplest way to get started is to use this plugin in conjunction with the [droolsjbpm-atomikos-integration](https://github.com/herwix/droolsjbpm-atomikos-integration) plugin:

1. Setup the [Atomikos](http://grails.org/plugin/atomikos) plugin
2. Clone both repositories (droolsjbpm-core and droolsjbpm-atomikos-integration)
3. Upgrade, compile and maven-install both plugins into your local maven-repo
4. Add their dependencies to your buildconfig.groovy plugin section
5. Exclude the 'javax.transaction:jta:1.1' from your core dependencies
6. You probably need to uncomment  //mavenRepo "http://download.java.net/maven/2/" in your buildconfig.groovy to resolve the jta dependency added by the droolsjbpm-atomikos-integration plugin.

That's it! The app should be able to start and droolsjbpm-core is automatically configured to use the atomikos transaction-manager!

Getting Started
=============

This plugin currently provides two easy ways to work with drools/jbpm resources and it provides a command line script to set them up. Just type:
 
> *grails*  *droolsjbpm* 

and follow the instructions. 

The source code of the plugin is also pretty well documented and the plugins themselves are set up to run standalone to complete some tests. So have a look there if you run into problems. 

Contributing
===========
If you find some bugs or have any requests/suggestions don't hesitate to open an issue or make a pull request. However, pull requests can only be accepted if a contributors agreement was signed. Please have a look at [contributing](https://github.com/herwix/droolsjbpm-core/blob/master/CONTRIBUTING) for further details.
