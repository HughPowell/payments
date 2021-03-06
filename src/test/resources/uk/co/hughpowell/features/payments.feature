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

	Scenario: Alice fails to update a payment
		When Alice updates a non-existant payment
		Then she gets an error indicating there is a conflict

	Scenario: Alice deletes a payment she made to Bob
		Given that Alice has made a payment to Bob
		When she deletes it
		Then she should no longer be able to fetch it

	Scenario: Alice fetches a collection of payments
		When Alice makes 3 payments
		Then she should be able to fetch a list of the 3 of them

	Scenario: Alice and Bob make simultaneous updates
		Given that Alice has made a payment to Bob
		When Alice and Bob both update it
		Then the conflict is detected
