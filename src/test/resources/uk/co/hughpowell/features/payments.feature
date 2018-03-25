Feature: Payments

	Scenario: Alice makes a payment to Bob
		When Alice makes a payment to Bob
		Then they are able to fetch that payment

	Scenario: Alice fails to fetch a non-existant payment
		When the payment Alice wishes to fetch does not exist
		Then she gets an error saying the payment does not exist

	Scenario: Alice updates a payment she made to Bob
		Given that Alice has made a payment to Bob for 100 pounds
		When she updates it to 200 pounds
		Then she should see 200 pounds when she fetches the payment

	Scenario: Alice deletes a payment she made to Bob
		Given that Alice has made a payment to Bob
		When she deletes it
		Then she should no longer be able to fetch it
