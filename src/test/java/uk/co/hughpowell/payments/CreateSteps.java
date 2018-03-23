package uk.co.hughpowell.payments;

import cucumber.api.java8.En;

public class CreateSteps extends StepsAbstractClass implements En {
	public CreateSteps() {
		When("^Alice makes a payment to Bob$", () -> {
			assert(true);
		});
		Then("^Alice is able to view that payment$", () -> {
			assert(true);
		});
	}
}