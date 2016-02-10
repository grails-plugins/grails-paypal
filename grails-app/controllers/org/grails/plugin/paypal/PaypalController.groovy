package org.grails.plugin.paypal

import grails.core.GrailsApplication

class PaypalController {
    GrailsApplication grailsApplication

	static allowedMethods = [buy: 'POST', notifyPaypal: 'POST']

	def notifyPaypal = {
		log.debug "Received IPN notification from PayPal Server ${params}"
		def config = grailsApplication.config.grails.paypal
		def server = config.server
		def login = params.email ?: config.email
		if (!server || !login) throw new IllegalStateException("Paypal misconfigured! You need to specify the Paypal server URL and/or account email. Refer to documentation.")

		params.cmd = "_notify-validate"
		def queryString = params.toQueryString()[1..-1]

		log.debug "Sending back query $queryString to PayPal server $server"
		def url = new URL(server)
		def conn = url.openConnection()
		conn.doOutput = true
		def writer = new OutputStreamWriter(conn.getOutputStream())
		writer.write queryString
		writer.flush()

		def result = conn.inputStream.text?.trim()

		log.debug "Got response from PayPal IPN $result"

		def payment = Payment.findByTransactionId(params.transactionId)

		if (payment && result == 'VERIFIED') {
			if (params.receiver_email != login) {
				log.warn """WARNING: receiver_email parameter received from PayPal does not match configured e-mail. This request is possibly fraudulent!
REQUEST INFO: ${params}
				"""
			}
			else {
				request.payment = payment
				def status = params.payment_status
				if (payment.status != Payment.COMPLETE && payment.status != Payment.CANCELLED) {
					if (payment.paypalTransactionId && payment.paypalTransactionId == params.txn_id) {
						log.warn """WARNING: Request tried to re-use and old PayPal transaction id. This request is possibly fraudulent!
		REQUEST INFO: ${params} """
					}
					else if (status == 'Completed') {
						payment.paypalTransactionId = params.txn_id
						payment.status = Payment.COMPLETE
						updateBuyerInformation(payment, params)
						log.info "Verified payment ${payment} as COMPLETE"
					} else if (status == 'Pending') {
						payment.paypalTransactionId = params.txn_id
						payment.status = Payment.PENDING
						updateBuyerInformation(payment, params)
						log.info "Verified payment ${payment} as PENDING"
					} else if (status == 'Failed') {
						payment.paypalTransactionId = params.txn_id
						payment.status = Payment.FAILED
						updateBuyerInformation(payment, params)
						log.info "Verified payment ${payment} as FAILED"
					}
				}
				payment.save(flush: true)
			}
		}
		else {
			log.debug "Error with PayPal IPN response: [$result] and Payment: [${payment?.transactionId}]"
		}
		render "OK" // Paypal needs a response, otherwise it will send the notification several times!
	}

	void updateBuyerInformation(payment, params) {
		BuyerInformation buyerInfo = payment.buyerInformation ?: new BuyerInformation()
		buyerInfo.populateFromPaypal(params)
		payment.buyerInformation = buyerInfo
	}

	def success = {
		def payment = Payment.findByTransactionId(params.transactionId)
		log.debug "Success notification received from PayPal for $payment with transaction id ${params.transactionId}"
		if (payment) {
			request.payment = payment
			if (payment.status != Payment.COMPLETE) {
				payment.status = Payment.COMPLETE
				payment.save(flush: true)
			}

			if (params.returnAction || params.returnController) {
				def args = [:]
				if (params.returnAction) args.action = params.returnAction
				if (params.returnController) args.controller = params.returnController
				args.params = params
				redirect(args)
			}
			else {
				return [payment: payment]
			}
		}
		else {
			response.sendError 403
		}
	}

	def cancel = {
		def payment = Payment.findByTransactionId(params.transactionId)
		log.debug "Cancel notification received from PayPal for $payment with transaction id ${params.transactionId}"
		if (payment) {
			request.payment = payment
			if (payment.status != Payment.COMPLETE) {
				payment.status = Payment.CANCELLED
				payment.save(flush: true)
				if (params.cancelAction || params.cancelController) {
					def args = [:]
					if (params.cancelAction) args.action = params.cancelAction
					if (params.cancelController) args.controller = params.cancelController
					args.params = params
					redirect(args)
				}
				else {
					return [payment: payment]
				}
			}
			else {
				response.sendError 403
			}
		}
		else {
			response.sendError 403
		}

	}

	def buy = {
        println(">>>>> START")
		def payment
		if (params.transactionId) {
            println(">>>>>>>>>>>> FOUND ID >>>")
			payment = Payment.findByTransactionId(params.transactionId)
		}
		else {
            println(">>>>>>>>>>>> creating new instance >>")
			payment = new Payment(params)
			payment.addToPaymentItems(new PaymentItem(params))
		}

        println(">>> AFTER IF ELSE block>>>>>>>>>>>")
		if (payment?.id) log.debug "Resuming existing transaction $payment"
		if (payment?.validate()) {
			request.payment = payment
            println(">>>> Before saving>>>>>>>>>>>>>> 1")
			payment.save(flush: true, failOnError: true)
            println("After saving >>>>>>>>>>>>>>>>>>>> 1")
			def config = grailsApplication.config.grails.paypal
			def server = config.server
			def baseUrl = params.baseUrl
			def login = params.email ?: config.email
			if (!server || !login) throw new IllegalStateException("Paypal misconfigured! You need to specify the Paypal server URL and/or account email. Refer to documentation.")

			def commonParams = [buyerId: payment.buyerId, transactionId: payment.transactionId]
			if (params.returnAction) {
				commonParams.returnAction = params.returnAction
			}
			if (params.returnController) {
				commonParams.returnController = params.returnController
			}
			if (params.cancelAction) {
				commonParams.cancelAction = params.cancelAction
			}
			if (params.cancelController) {
				commonParams.cancelController = params.cancelController
			}
			def notifyURL = g.createLink(absolute: baseUrl==null, base: baseUrl, controller: 'paypal', action: 'notifyPaypal', params: commonParams).encodeAsURL()
			def successURL = g.createLink(absolute: baseUrl==null, base: baseUrl, controller: 'paypal', action: 'success', params: commonParams).encodeAsURL()
			def cancelURL = g.createLink(absolute: baseUrl==null, base: baseUrl, controller: 'paypal', action: 'cancel', params: commonParams).encodeAsURL()

			def url = new StringBuffer("$server?")
			url << "cmd=_xclick&"
			url << "business=$login&"
			url << "item_name=${payment.paymentItems[0].itemName}&"
			url << "item_number=${payment.paymentItems[0].itemNumber}&"
			url << "quantity=${payment.paymentItems[0].quantity}&"
			url << "amount=${payment.paymentItems[0].amount}&"
            if (payment.paymentItems[0].discountAmount > 0) {
                url << "discount_amount=${payment.paymentItems[0].discountAmount}&"
            }
			url << "tax=${payment.tax}&"
			url << "currency_code=${payment.currency}&"
			if (params.lc) 
			    url << "lc=${params.lc}&"
			url << "notify_url=${notifyURL}&"
			url << "return=${successURL}&"
			url << "cancel_return=${cancelURL}"

			log.debug "Redirection to PayPal with URL: $url"

            println(">>>>>>>>>>>>>>>>>>>>> URL to redirect if block ...   $url")
            redirect(url: url)
		}
		else {
			flash.payment = payment
            println(">>>>>>>>>>>>>>>>>>>>> URL to redirect else block ...   $params.originalURL")
			redirect(url: params.originalURL)
		}
	}

	def uploadCart = {ShippingAddressCommand address ->
		//Assumes the Payment has been pre-populated and saved by whatever cart mechanism
		//you are using...
		def payment = Payment.findByTransactionId(params.transactionId)
		log.debug "Uploading cart: $payment"
		def config = grailsApplication.config.grails.paypal
		def server = config.server
		def login = params.email ?: config.email
		if (!server || !login) throw new IllegalStateException("Paypal misconfigured! You need to specify the Paypal server URL and/or account email. Refer to documentation.")
		def commonParams = [buyerId: payment.buyerId, transactionId: payment.transactionId]
		if (params.returnAction) {
			commonParams.returnAction = params.returnAction
		}
		if (params.returnController) {
			commonParams.returnController = params.returnController
		}
		if (params.cancelAction) {
			commonParams.cancelAction = params.cancelAction
		}
		if (params.cancelController) {
			commonParams.cancelController = params.cancelController
		}
		def notifyURL = g.createLink(absolute: true, controller: 'paypal', action: 'notifyPaypal', params: commonParams).encodeAsURL()
		def successURL = g.createLink(absolute: true, controller: 'paypal', action: 'success', params: commonParams).encodeAsURL()
		def cancelURL = g.createLink(absolute: true, controller: 'paypal', action: 'cancel', params: commonParams).encodeAsURL()

		def url = new StringBuffer("$server?")
		url << "cmd=_cart&upload=1&"
		url << "business=$login&"
		if (params.pageStyle) {
			url << "page_style=${params.pageStyle}&"
		}
		if (params.addressOverride) {
			url << "address_override=1&"
			url << "first_name=${address.firstName}&"
			url << "last_name=${address.lastName}&"
			url << "address1=${address.addressLineOne}&"
			if (address.addressLineTwo) {
				url << "address2=${address.addressLineTwo}&"
			}
			url << "city=${address.city}&"
			url << "country=${address.country}&"
			url << "night_phone_a=${address.areaCode}&"
			url << "night_phone_b=${address.phonePrefix}&"
			url << "night_phone_c=${address.phoneSuffix}&"
			url << "state=${address.state}&"
			url << "zip=${address.zipCode}&"
		}
		else if (params.noShipping) {
			url << "no_shipping=1&"
		}
		payment.paymentItems.eachWithIndex {paymentItem, i ->
			def itemId = i + 1
			url << "item_name_${itemId}=${paymentItem.itemName}&"
			url << "item_number_${itemId}=${paymentItem.itemNumber}&"
			url << "quantity_${itemId}=${paymentItem.quantity}&"
			url << "amount_${itemId}=${paymentItem.amount}&"
            if (payment.discountCartAmount == 0 && paymentItem.discountAmount > 0) {
                url << "discount_amount_${itemId}=${paymentItem.discountAmount}&"
            }
		}
        if (payment.discountCartAmount > 0) {
            url << "discount_cart_amount_${payment.discountCartAmount}&"
        }
		url << "currency_code=${payment.currency}&"
		url << "notify_url=${notifyURL}&"
		url << "return=${successURL}&"
		url << "cancel_return=${cancelURL}&"
		url << "rm=2"

		log.debug "Redirection to PayPal with URL: $url"

		redirect(url: url)
	}

}

// This is a first version that only applies to the U.S. - Can anybody write an i18n-enabled version
// that Paypal can still understand?

class ShippingAddressCommand {
	String firstName
	String lastName
	String addressLineOne
	String addressLineTwo
	String city
	USState state
	String country = 'US'
	String zipCode
	String areaCode
	String phonePrefix
	String phoneSuffix

	static constraints = {
		firstName(blank: false)
		lastName(blank: false)
		addressLineOne(blank: false)
		addressLineTwo(nullable: true, blank: true)
		city(blank: false)
		country(blank: false)
		zipCode(blank: false, matches: /\d{5}/)
		areaCode(blank: false, matches: /\d{3}/)
		phonePrefix(blank: false, matches: /\d{3}/)
		phoneSuffix(blank: false, matches: /\d{4}/)
	}

}

