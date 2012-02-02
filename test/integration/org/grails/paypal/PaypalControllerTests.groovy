package org.grails.paypal

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PaypalControllerTests extends GroovyTestCase {
	def oldConfig

	void setUp() {
		oldConfig = ConfigurationHolder.config
		def config = new ConfigObject()
		config.grails.paypal.server = "https://www.sandbox.paypal.com/cgi-bin/webscr"
		config.grails.paypal.email = "test@g2one.com"

		ConfigurationHolder.config = config
	}

	void tearDown() {
		ConfigurationHolder.config = oldConfig
	}

	void testBuyInvalidPayment() {
		def controller = new PaypalController()
		controller.params.originalURL = "/start/page"
		controller.buy()

		assertEquals "/start/page", controller.response.getRedirectedUrl()
	}

	void testBuyValidPayment() {
		def controller = new PaypalController()
		controller.params.originalURL = "/start/page"
		controller.params.itemName = "iPod"
		controller.params.itemNumber = "IP390483"
		controller.params.buyerId = "10"
		controller.params.amount = "200.00"
		controller.params.baseUrl = 'http://myowndomain.com:8180/'
		controller.buy()

//		https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://myowndomain.com:8180/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

		def url = controller.response.getRedirectedUrl()
println "\n${url}\n"
		assertTrue url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

		assertTrue url.indexOf("cmd=_xclick") > -1
		assertTrue url.indexOf("business=test@g2one.com") > -1
		assertTrue url.indexOf("item_name=iPod") > -1
		assertTrue url.indexOf("item_number=IP390483") > -1
		assertTrue url.indexOf("amount=200.0") > -1
		assertTrue url.indexOf("quantity=1") > -1
		assertTrue url.indexOf("return=http%3A%2F%2Fmyowndomain.com%3A8180") > -1

		assertEquals 1, Payment.count()
		def payment = Payment.findByBuyerId("10")

		assert payment
		assertEquals 10, payment.buyerId
		assertEquals 200.00, payment.paymentItems[0].amount
		assertEquals "iPod", payment.paymentItems[0].itemName
		assertEquals "IP390483", payment.paymentItems[0].itemNumber

		assertTrue payment.transactionId.startsWith("TRANS-10-")
	}

	void testUploadCartNoShipping() {
		def payment = new Payment()
		payment.buyerId = 10

		PaymentItem paymentItem = new PaymentItem()
		paymentItem.amount = 200.00
		paymentItem.itemName = "iPod"
		paymentItem.itemNumber = "IP390483"
		payment.addToPaymentItems(paymentItem)

		paymentItem = new PaymentItem()
		paymentItem.amount = 299.00
		paymentItem.itemName = "iPhone"
		paymentItem.itemNumber = "IP987123"
		payment.addToPaymentItems(paymentItem)

		payment.save(flush: true)
		def transactionId = payment.transactionId

		def controller = new PaypalController()
		controller.params.originalURL = "/start/page"
		controller.params.transactionId = transactionId
		controller.params.noShipping = 'true'
		controller.uploadCart()

//		https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://localhost:8080/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

		def url = controller.response.getRedirectedUrl()

		assertTrue url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

		assertTrue url.indexOf("cmd=_cart&upload=1&") > -1
		assertTrue url.indexOf("business=test@g2one.com") > -1
		assertTrue url.indexOf("no_shipping=1") > -1
		assertTrue url.indexOf("item_name_1=iPod") > -1
		assertTrue url.indexOf("item_number_1=IP390483") > -1
		assertTrue url.indexOf("amount_1=200.0") > -1
		assertTrue url.indexOf("quantity_1=1") > -1
		assertTrue url.indexOf("item_name_2=iPhone") > -1
		assertTrue url.indexOf("item_number_2=IP987123") > -1
		assertTrue url.indexOf("amount_2=299.0") > -1
		assertTrue url.indexOf("quantity_2=1") > -1
		assertTrue url.indexOf("localhost%3A8080") > -1

		assertEquals 1, Payment.count()
		payment = Payment.findByBuyerId("10")

		assert payment
		assertEquals 10, payment.buyerId
		assertEquals 200.00, payment.paymentItems[0].amount
		assertEquals "iPod", payment.paymentItems[0].itemName
		assertEquals "IP390483", payment.paymentItems[0].itemNumber

		assertTrue payment.transactionId.startsWith("TRANS-10-")
	}

	void testUploadCartValidPayment() {
		def payment = new Payment()
		payment.buyerId = 10

		PaymentItem paymentItem = new PaymentItem()
		paymentItem.amount = 200.00
		paymentItem.itemName = "iPod"
		paymentItem.itemNumber = "IP390483"
		payment.addToPaymentItems(paymentItem)

		paymentItem = new PaymentItem()
		paymentItem.amount = 299.00
		paymentItem.itemName = "iPhone"
		paymentItem.itemNumber = "IP987123"
		payment.addToPaymentItems(paymentItem)

		payment.save(flush: true)
		def transactionId = payment.transactionId

		def controller = new PaypalController()
		controller.params.originalURL = "/start/page"
		controller.params.transactionId = transactionId
		controller.uploadCart()

//		https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://localhost:8080/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

		def url = controller.response.getRedirectedUrl()

		assertTrue url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

		assertTrue url.indexOf("cmd=_cart&upload=1&") > -1
		assertTrue url.indexOf("business=test@g2one.com") > -1
		assertTrue url.indexOf("item_name_1=iPod") > -1
		assertTrue url.indexOf("item_number_1=IP390483") > -1
		assertTrue url.indexOf("amount_1=200.0") > -1
		assertTrue url.indexOf("quantity_1=1") > -1
		assertTrue url.indexOf("item_name_2=iPhone") > -1
		assertTrue url.indexOf("item_number_2=IP987123") > -1
		assertTrue url.indexOf("amount_2=299.0") > -1
		assertTrue url.indexOf("quantity_2=1") > -1

		assertEquals 1, Payment.count()
		payment = Payment.findByBuyerId("10")

		assert payment
		assertEquals 10, payment.buyerId
		assertEquals 200.00, payment.paymentItems[0].amount
		assertEquals "iPod", payment.paymentItems[0].itemName
		assertEquals "IP390483", payment.paymentItems[0].itemNumber

		assertTrue payment.transactionId.startsWith("TRANS-10-")
	}

	void testUploadCartWithShippingAddress() {
		def payment = new Payment()
		payment.buyerId = 10

		PaymentItem paymentItem = new PaymentItem()
		paymentItem.amount = 200.00
		paymentItem.itemName = "iPod"
		paymentItem.itemNumber = "IP390483"
		payment.addToPaymentItems(paymentItem)

		paymentItem = new PaymentItem()
		paymentItem.amount = 299.00
		paymentItem.itemName = "iPhone"
		paymentItem.itemNumber = "IP987123"
		payment.addToPaymentItems(paymentItem)

		payment.save(flush: true)
		def transactionId = payment.transactionId

		def controller = new PaypalController()
		controller.params.originalURL = "/start/page"
		controller.params.transactionId = transactionId
		controller.params.addressOverride = 'true'
		controller.params.firstName = 'Matt'
		controller.params.lastName = 'Stine'
		controller.params.addressLineOne = 'Memphis Java User Group'
		controller.params.addressLineTwo = '160 Shadyac Avenue'
		controller.params.city = 'Memphis'
		controller.params.state = 'TN'
		controller.params.zipCode = '38105'
		controller.params.areaCode = '901'
		controller.params.phonePrefix = '493'
		controller.params.phoneSuffix = '5546'
		controller.uploadCart()

//		https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_xclick&business=testpp_1211202427_biz@g2one.com&item_name=iPod&item_number=IP390483&amount=200.0&currency_code=USD&notify_url=http://localhost:8080/paypal/notify?buyerId=10&return=http://localhost:8080/paypal/success?buyerId=10&cancel_return=http://localhost:8080/paypal/cancel?buyerId=10

		def url = controller.response.getRedirectedUrl()

		assertTrue url.startsWith("https://www.sandbox.paypal.com/cgi-bin/webscr")

		assertTrue url.indexOf("cmd=_cart&upload=1&") > -1
		assertTrue url.indexOf("business=test@g2one.com") > -1
		assertTrue url.indexOf("item_name_1=iPod") > -1
		assertTrue url.indexOf("item_number_1=IP390483") > -1
		assertTrue url.indexOf("amount_1=200.0") > -1
		assertTrue url.indexOf("quantity_1=1") > -1
		assertTrue url.indexOf("item_name_2=iPhone") > -1
		assertTrue url.indexOf("item_number_2=IP987123") > -1
		assertTrue url.indexOf("amount_2=299.0") > -1
		assertTrue url.indexOf("quantity_2=1") > -1

		assertTrue url.indexOf("address_override=1&") > -1
		assertTrue url.indexOf("first_name=Matt") > -1
		assertTrue url.indexOf("last_name=Stine") > -1
		assertTrue url.indexOf("address1=Memphis Java User Group") > -1
		assertTrue url.indexOf("address2=160 Shadyac Avenue") > -1
		assertTrue url.indexOf("city=Memphis") > -1
		assertTrue url.indexOf("country=US") > -1
		assertTrue url.indexOf("night_phone_a=901") > -1
		assertTrue url.indexOf("night_phone_b=493") > -1
		assertTrue url.indexOf("night_phone_c=5546") > -1
		assertTrue url.indexOf("state=TN") > -1
		assertTrue url.indexOf("zip=38105") > -1

		assertEquals 1, Payment.count()
		payment = Payment.findByBuyerId("10")

		assert payment
		assertEquals 10, payment.buyerId
		assertEquals 200.00, payment.paymentItems[0].amount
		assertEquals "iPod", payment.paymentItems[0].itemName
		assertEquals "IP390483", payment.paymentItems[0].itemNumber

		assertTrue payment.transactionId.startsWith("TRANS-10-")
	}
}

