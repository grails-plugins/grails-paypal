package org.grails.plugin.paypal

class Payment implements Serializable {
    static final PENDING = 'PENDING'
    static final INVALID = 'INVALID'
    static final FAILED = 'FAILED'
    static final COMPLETE = 'COMPLETE'
    static final CANCELLED = 'CANCELLED'

    List paymentItems
    String transactionId
    String paypalTransactionId
    String status = PENDING
    Double tax = 0 // tax applies to entire payment, not to each item!
    BigDecimal discountCartAmount = 0 // optional currency value; if specified will override individual item discounts
    Currency currency = Currency.getInstance("USD") // default to USD
    Long buyerId
    BuyerInformation buyerInformation // details, provided by Paypal
    BigDecimal shipping = 0.0
    BigDecimal gross = 0.0
    def transactionIdPrefix = "TRANS"

    static hasMany = [paymentItems: PaymentItem]

    transient beforeInsert = {
        transactionId = "${transactionIdPrefix}-$buyerId-${System.currentTimeMillis()}"
    }

    static constraints = {
        status inList: [PENDING, INVALID, FAILED, COMPLETE, CANCELLED]
        transactionId nullable: true
        paypalTransactionId nullable: true
        buyerInformation nullable: true
    }

    static mapping = {
        autoImport false
    }

    @Override
    String toString() { "Payment: ${transactionId ?: 'not saved'}" }
}
