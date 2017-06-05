# grails-paypal
Integrates Grails with Paypal IPN

Summary
-------
This plug-in allows Grails applications to integrate with Paypal and its Instant Payment Notification (IPN) system.

A PayPalController is provided that has a "notifyPaypal" action which deals with responses from the PayPal IPN. In order for this to function you need to enable IPN in your PayPal Profile under Profile / Instant Payment Notification Preferences and provide PayPal with the URL you have mapped the "notify" action to.

In order for this plug-in to function you must configure the following settings in application.groovy:

* grails.paypal.server - The URL of the paypal server
* grails.paypal.email - The email of the merchant account
* grails.serverURL - The server URL for production

Description
-----------
There are several ways to use this plugin in an application.

When used in a multi-project build:
```groovy
 grails {
     plugins {
         compile project(':mybusiness')
     }
 }
```
 
When used as a normal dependency in dependencies block:
```groovy
compile "org.grails.plugins.paypal:grails-paypal"
```

Usage
-----
This plug-in provides 3 new artefacts:
 
* A PaypalController - a Grails controller that deals with PayPal requests and integrations with the PayPal Instance Payment Notification (IPN) system
* A PaypalTagLib - a tag library that provides the ability to create PayPal "buy now" buttons
* A Payment domain class - a domain class that is used to store information about Payments
 
To get started you need to configure PayPal server and merchant email address in application.groovy as well as the grails server URL used for absolute links. For example:

```groovy
environments { 
   production {
      grails.paypal.server = "https://www.paypal.com/cgi-bin/webscr"
      grails.paypal.email = "example@business.com"
      grails.serverURL = "http://www.grails.org"	  // Beware this is an application-wide setting	
   }
   development {
      grails.paypal.server = "https://www.sandbox.paypal.com/cgi-bin/webscr"
      grails.paypal.email = "testpp_1211202427_biz@g2one.com"
      grails.serverURL = "http://812.99.101.131"				
   }
 }
 ```

With this done the next thing to do is to create a PayPal button. For example:
```gsp
<paypal:button 
 	itemName="iPod Nano"
 	itemNumber="IPD0843403"
 	transactionId="${payment?.transId}"
 	amount="99.00"
 	buyerId="${user.id}"
 	/>
```
The itemName, itemNumber, amount and buyerId are required. The buyerId can be a reference to an existing domain class 
id such as a User id. The transactionId is used in case you are resuming an existing Payment.

When the button is clicked the plugin will create and save a new instance of the org.grails.paypal.Payment class to 
track the order. The button will also send the IPN URL that PayPal should use to send notifications back.

> In order for IPN to function correctly you must set the grails.serverURL setting in application.groovy to a web facing 
> domain or IP address (so that PayPal can do a post back)
 
Payment Tracking with Filters
-----------------------------
A typical way to integrate with the PayPal plug-in is through the use of interceptors. The PayPal plug-in places the payment instance into the request when it has been created so an interceptor that executes after the PaypalController and integrate with it.

For example:
```groovy
class PaypalBuyInterceptor {

    PaypalBuyInterceptor() {
        match(controller:"paypal", action:"buy")
    }

    boolean before() { true }

    boolean after() {
        new IPodPayment(payment:request.payment, IPod.get(params.id)).save()
    }

    void afterView() {
        // no-op
    }
}

class PaymentReceivedInterceptor {

    PaymentReceivedInterceptor() {
        match(controller:'paypal', action:'(success|notifyPaypal)')
    }

    boolean before() { true }

    boolean after() {
        def payment = request.payment
        if (payment && payment.status == org.grails.paypal.Payment.COMPLETE) {
             def payment = IPodPayment.findByPayment(request.payment)
             payment.shipIt()
        }
    }

    void afterView() {
        // no-op
    }
}
```
Returning to several different domains from Paypal
--------------------------------------------------
You can provide Paypal with a domain different from "grails.serverURL" just in case you need it. A typical case would be handling several domains with the same application, so your user follows where it started.
```groovy
class PaypalBuyInterceptor {

    PaypalBuyInterceptor() {
        match(controller:"paypal", action:"buy")
    }

    boolean before() {
        params.baseUrl = "/** whatever domain you want Paypal to return once the payment is done *//"
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
```
