<g:each var="paymentItem" in="${payment.paymentItems}">
	<div class="transSummaryItem">
		<span class="transSummaryItemName">Item Name:</span>
		<span class="transSummaryItemValue">${paymentItem.itemName.encodeAsHTML()}</span>
	</div>
	<div class="transSummaryItem">
		<span class="transSummaryItemName">Quantity:</span>
		<span class="transSummaryItemValue">${paymentItem.quantity}</span>
	</div>
	<div class="transSummaryItem">
		<span class="transSummaryItemName">Price:</span>
		<span class="transSummaryItemValue">${paymentItem.amount * paymentItem.quantity}</span>
	</div>
</g:each>