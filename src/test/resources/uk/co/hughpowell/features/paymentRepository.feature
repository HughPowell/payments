Feature: Payments Repository

	Scenario: Nancy creates a payment
		When Nancy creates a payment in the repository
		Then She can retrieve it
