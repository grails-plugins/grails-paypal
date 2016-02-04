environments {
	production {
		grails.paypal.server = "https://www.paypal.com/cgi-bin/webscr"
		grails.paypal.email = "example@business.com"
		grails.serverURL = "http://www.grails.org"
	}
	development {
		grails.paypal.server = "https://www.sandbox.paypal.com/cgi-bin/webscr"
		grails.paypal.email = "seller_1237686842_biz@mattstine.com"
		grails.serverURL = "http://localhost:8080"
	}
}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
