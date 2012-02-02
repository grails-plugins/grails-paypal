<html>
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8">
		<title>Paypal Test</title>
	</head>

	<body id="body">
        Buy Now: item, no discount.. 
		<paypal:button itemName="iPod Nano"
		               itemNumber="IPD32048039"
		               amount="99.00"
		               buyerId="10"
		/>

        Buy Now: item, with discount.. 
		<paypal:button itemName="iPod Nano"
		               itemNumber="IPD32048039"
		               amount="99.00"
		               discountAmount="9.00"
		               buyerId="10"
		/>

        Cart upload..
        <%
            if (org.grails.paypal.Payment.count() == 0) {
                payment = new org.grails.paypal.Payment(buyerId:10)
                payment.addToPaymentItems(
                    new org.grails.paypal.PaymentItem(
                        amount: 99,
                        discountAmount: 9,
                        itemName: "iPod Nano", 
                        itemNumber: "IPD32048039"
                    )
                )
                payment.save(flush:true)
            }
            else
                payment = org.grails.paypal.Payment.list()[0]
        %>

        <g:form
                controller="paypal"
                action="uploadCart"
                params="[transactionId:payment.transactionId]"
            >
            <input type="image" class="paypal"
                src="https://www.paypalobjects.com/WEBSCR-640-20110306-1/en_US/i/btn/btn_xpressCheckout.gif"
                alt="Click to pay via PayPal - the safer, easier way to pay"/>
        </g:form>
	</body>
</html>
