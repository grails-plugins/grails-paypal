package org.grails.plugin.paypal

import grails.testing.gorm.DomainUnitTest
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Rollback
@Integration
class PaymentIntegrationSpec extends Specification implements DomainUnitTest<Payment> {
	void testValidatePayment() {
        when:
		def payment = new Payment()
        then:
		assert !payment.validate()

        when:
		PaymentItem paymentItem = new PaymentItem()
		paymentItem.amount = 10.00
		paymentItem.itemName = "iPod"
		paymentItem.itemNumber = "IP01901"
		payment.addToPaymentItems(paymentItem)
		payment.buyerId = 10

        then:
		assert payment.validate()
		assert payment.save(flush:true)
        assert payment.transactionId.startsWith("TRANS-10-")
	}

	void testTransactionPrefix() {
        when:
		def payment = new Payment()
        then:
		assert !payment.validate()

        when:
		PaymentItem paymentItem = new PaymentItem()
		paymentItem.amount = 10.00
		paymentItem.itemName = "iPod"
		paymentItem.itemNumber = "IP01901"
		payment.addToPaymentItems(paymentItem)
		payment.buyerId = 10
        payment.transactionIdPrefix = "FOO"

        then:
		assert payment.validate()
		assert payment.save(flush:true)
        assert payment.transactionId.startsWith("FOO-10-")
	}
}
