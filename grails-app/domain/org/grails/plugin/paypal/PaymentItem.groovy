package org.grails.plugin.paypal

class PaymentItem implements Serializable {
	BigDecimal amount
    BigDecimal discountAmount = 0
	BigDecimal weight = 0.0 // added for ship weight
	String itemName
	String itemNumber
	Integer quantity = 1

	static belongsTo = [payment:Payment]

	static constraints = {
		itemName blank:false
		itemNumber blank:false
	}
}
