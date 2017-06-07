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
<g:if test="${payment?.shipping}">
    <div>
        <span>Shipping:</span>
        <span>${payment.shipping}</span>
    </div>
</g:if>
<g:if test="${payment?.tax}">
    <div>
        <span>Tax:</span>
        <span>${payment.tax}</span>
    </div>
</g:if>
<g:if test="${payment?.gross}">
    <div>
        <span>Total:</span>
        <span>${payment.gross} ${payment.currency}</span>
    </div>
</g:if>