package org.grails.paypal

class BuyerInformation implements Serializable {

	String uniqueCustomerId

	String firstName
	String lastName
	String companyName
	String receiverName // included when the customer provides a Gift Address
	String email
	String street
	String zip
	String city
	String state
	String country
	String countryCode
	String phoneNumber

	boolean addressConfirmed

	static belongsTo = Payment

	static constraints = { // everything nullable - need to store this object even when values are missing!
		uniqueCustomerId nullable: true, blank: true
		firstName nullable: true, blank: true
		lastName nullable: true, blank: true
		companyName nullable: true, blank: true
		receiverName nullable: true, blank: true
		email nullable: true, blank: true
		street nullable: true, blank: true
		zip nullable: true, blank: true
		city nullable: true, blank: true
		state nullable: true, blank: true
		country nullable: true, blank: true
		countryCode nullable: true, blank: true
		phoneNumber nullable: true, blank: true
	}

	void populateFromPaypal(Map paypalArgs) {
		uniqueCustomerId = paypalArgs.payer_id
		firstName = paypalArgs.first_name
		lastName = paypalArgs.last_name
		companyName = paypalArgs.payer_business_name
		receiverName = paypalArgs.address_name
		email = paypalArgs.payer_email
		street = paypalArgs.address_street
		zip = paypalArgs.address_zip
		city = paypalArgs.address_city
		state = paypalArgs.address_state
		country = paypalArgs.address_country
		countryCode = paypalArgs.address_country_code
		phoneNumber = paypalArgs.contact_phone
		addressConfirmed = (paypalArgs.address_status == 'confirmed')
	}
}
