<html>
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8">
		<title>Transaction Cancelled</title>
	</head>
	<body id="body">
		Your purchase transaction has been cancelled. Information about the items you planned to purchase can be seen below:
		<div id="transactionSummary" class="transactionSummary">
			<g:render template="txsummary" model="[payment:payment]" plugin="paypal" />
		</div>
	</body>
</html>
