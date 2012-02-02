package org.grails.paypal

class Payment implements Serializable {
	static final PENDING = 'PENDING'
	static final INVALID = 'INVALID'
	static final FAILED = 'FAILED'
	static final COMPLETE = 'COMPLETE'
	static final CANCELLED = 'CANCELLED'

	static hasMany = [paymentItems:PaymentItem]

	List paymentItems
	String transactionId
	String paypalTransactionId
	String status = PENDING

	Double tax = 0 // tax applies to entire payment, not to each item!
    BigDecimal discountCartAmount = 0 // optional currency value; if specified will override individual item discounts

	Currency currency = Currency.getInstance("USD") // default to USD
	Long buyerId

	BuyerInformation buyerInformation // details, provided by Paypal

    def transactionIdPrefix = "TRANS"

	transient beforeInsert = {
		transactionId = "${transactionIdPrefix}-$buyerId-${System.currentTimeMillis()}"
	}

	String toString() { "Payment: ${transactionId ?: 'not saved'}"}

	static constraints = {
		status inList: [Payment.PENDING, Payment.INVALID, Payment.FAILED,Payment.COMPLETE,Payment.CANCELLED]
		transactionId nullable: true
		paypalTransactionId nullable: true
		buyerInformation nullable: true
	}
}
