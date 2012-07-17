class PaypalGrailsPlugin {
	def version = "0.6.8"
	def grailsVersion = "1.1 > *"
	def pluginExcludes = [
		"grails-app/views/paypal/test.gsp"
	]

	def author = "Matthias Bohlen, (originally by Graeme Rocher, followed by Matt Stine)"
	def authorEmail = "mbohlen@mbohlen.de, matt@mattstine.com"

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

	def documentation = "http://grails.org/plugin/paypal"
}

