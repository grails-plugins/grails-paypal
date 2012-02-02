package org.grails.paypal

class PaypalTagLib {

	static namespace = "paypal"

	def errors = { attrs ->
		attrs.bean = flash.payment
		g.renderErrors(attrs)
	}

	def button = { attrs ->
		def config = grailsApplication.config.grails.paypal
		def itemName = attrs.itemName
		def itemNumber = attrs.itemNumber
		def amount = attrs.amount
		def discountAmount = attrs.discountAmount
		def tax = attrs.tax ?: "0.0"
		def buyerId = attrs.buyerId
		def buttonSrc = attrs.buttonSrc ?: "https://www.paypal.com/en_US/i/btn/btn_buynow_LG.gif"
		def buttonAlt = attrs.buttonAlt ?: "PayPal - The safer, easier way to pay online!"
		def currency = attrs.currency ?: Currency.getInstance("USD")
		def returnAction = attrs.returnAction ? "${hiddenField(name:'returnAction', value:attrs.returnAction)}" : ""
		def returnController = attrs.returnController ? "${hiddenField(name:'returnController', value:attrs.returnController)}" : ""
		def cancelAction = attrs.cancelAction ? "${hiddenField(name:'cancelAction', value:attrs.cancelAction)}" : ""
		def cancelController = attrs.cancelController ? "${hiddenField(name:'cancelController', value:attrs.cancelController)}" : ""
		def originalURL = attrs.originalURL ? "${hiddenField(name:'originalURL', value:attrs.originalURL)}" : ""
		def additionalParams = new StringBuffer()
		attrs.params?.each { k,v -> additionalParams << "${hiddenField(name:k, value:v)}"  }
		attrs.remove('params')

		if(!config.server || !config.email) {
			out << '<div style="border:1px solid red; padding:5px;">Paypal is miconfigured. You need to specify the "grails.paypal.server" and "grails.paypal.email" variables</div>'
		}
		else if(!itemNumber || !itemName || !amount || !buyerId) {
			out << "<div style=\"border:1px solid red; padding:5px;\">Paypal button error: One of required attributes missing (itemName=${itemName}, itemNumber=${itemNumber}, amount=${amount} or buyerId=${buyerId})</div>"
		}
		else {
			def formParams = [:]
			if(attrs.transactionId) formParams.transactionId = attrs.transactionId
			out << g.form(controller:"paypal",action:"buy", params:formParams) {
				""" ${returnAction}
				    ${returnController}
				    ${cancelAction}
				    ${cancelController}
					${additionalParams}
					${originalURL}
					${hiddenField(name:'itemName', value:itemName)}
					${hiddenField(name:'itemNumber', value:itemNumber)}
					${hiddenField(name:'amount', value:amount)}
					${hiddenField(name:'discountAmount', value:discountAmount)}
					${hiddenField(name:'tax', value:tax)}
					${hiddenField(name:'buyerId', value:buyerId)}
					${hiddenField(name:'currency', value:currency)}
					<input type=\"image\" src=\"${buttonSrc}\" border=\"0\" name=\"submit\" alt=\"${buttonAlt}\">
					<img alt="" border=\"0\" src=\"https://www.paypal.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">
					"""
			}
		}
	}
}
