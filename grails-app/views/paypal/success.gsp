<html>
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8">
		<title>Transaction Complete</title>
	</head>
	<body id="body">
		Your purchase is complete. Information for your reference can be seen below:
		<div id="transactionSummary" class="transactionSummary">
			<div class="transSummaryItem">
				<span class="transSummaryItemName">Transaction ID:</span>
				<span class="transSummaryItemValue">${payment.transactionId}</span>
			</div>
			<g:render template="txsummary" model="[payment:payment]"/>
		</div>
	</body>
</html>