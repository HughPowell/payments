Feature: Payments

	Scenario: Alice makes a payment to Bob
		When Alice makes a payment to Bob
		Then They are able to view that payment

	Scenario: Alice fails to view a non-existant payment
		When The payment Alice wishes to view does not exist
		Then She gets an error saying the payment does not exist
