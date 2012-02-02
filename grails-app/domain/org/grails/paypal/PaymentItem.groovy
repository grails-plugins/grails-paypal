package org.grails.paypal

class PaymentItem implements Serializable {
	BigDecimal amount
    BigDecimal discountAmount = 0
	String itemName
	String itemNumber
	Integer quantity = 1

	static belongsTo = [payment:Payment]

	static constraints = {
		itemName blank:false
		itemNumber blank:false
	}
}
