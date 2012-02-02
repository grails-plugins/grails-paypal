import grails.util.GrailsUtil

class PaypalUrlMappings {
	static mappings = {
		"/paypal/buy/$transactionId?"(controller:"paypal", action:"buy")
		"/paypal/cart/$transactionId"(controller:"paypal", action:"uploadCart")
		"/paypal/notify/$buyerId/$transactionId"(controller:"paypal", action:"notify")
		"/paypal/success/$buyerId/$transactionId"(controller:"paypal", action:"success")
		"/paypal/cancel/$buyerId/$transactionId"(controller:"paypal", action:"cancel")

		if (GrailsUtil.isDevelopmentEnv()) {
			"/paypal/test"(view:"/paypal/test")
		}
	}
}
