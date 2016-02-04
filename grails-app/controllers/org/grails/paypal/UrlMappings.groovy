package org.grails.paypal

import grails.util.GrailsUtil

class UrlMappings {
	static mappings = {
		"/paypal/buy/$transactionId?"(controller:"paypal", action:"buy")
		"/paypal/cart/$transactionId"(controller:"paypal", action:"uploadCart")
		"/paypal/notifyPaypal/$buyerId/$transactionId"(controller:"paypal", action:"notifyPaypal")
		"/paypal/success/$buyerId/$transactionId"(controller:"paypal", action:"success")
		"/paypal/cancel/$buyerId/$transactionId"(controller:"paypal", action:"cancel")

		if (GrailsUtil.isDevelopmentEnv()) {
			"/paypal/test"(view:"/paypal/test")
		}
	}
}
