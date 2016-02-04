package org.grails.paypal

import grails.plugins.Plugin

class PaypalGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.0 > *"

    def pluginExcludes = [
            "grails-app/views/paypal/test.gsp",
            "grails-app/views/error.gsp"
    ]

    def author = "Matthias Bohlen, (originally by Graeme Rocher, followed by Matt Stine)"
    def authorEmail = "mbohlen@mbohlen.de, matt@mattstine.com, mansi.arora@tothenew.com"

    def title = "Provides integration with Paypal's Instant Payment Notfication (IPN) system"
    def description = '''\
This plug-in allows Grails applications to integrate with Paypal and its Instant Payment Notification (IPN) system.

A PayPalController is provided that has a "notifyPaypal" action which deals with responses from the PayPal IPN. In order for this to function
you need to enable IPN in your PayPal Profile under Profile / Instant Payment Notification Preferences and provide PayPal with the URL
you have mapped the "notify" action to.

In order for this plug-in to function you must configure the following settings in Config.groovy:

* grails.paypal.server - The URL of the paypal server
* grails.paypal.email - The email of the merchant account
* grails.serverURL - The server URL for production
'''
    def profiles = ['web']
    def documentation = "http://grails.org/plugin/paypal"

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

    Closure doWithSpring() {
        { ->
            // TODO Implement runtime spring config (optional)
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}

