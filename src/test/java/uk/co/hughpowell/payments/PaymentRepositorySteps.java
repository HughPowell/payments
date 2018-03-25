package uk.co.hughpowell.payments;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import cucumber.api.java8.En;

public class PaymentRepositorySteps extends StepsAbstractClass implements En {
	final JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
	
	private JsonNode createPayment(String from, String to) {
		Map<String, JsonNode> paymentProperties = new HashMap<String, JsonNode>();
		JsonNode jsonUUID = jsonFactory.textNode(UUID.randomUUID().toString());
		paymentProperties.put("id", jsonUUID);
		paymentProperties.put("from", jsonFactory.textNode(from));
		paymentProperties.put("to", jsonFactory.textNode(to));
		return jsonFactory.objectNode().setAll(paymentProperties);
	}
	
	private PaymentsRepository repository = new PaymentsRepository();
	
	private JsonNode payment;
	
	@cucumber.api.java.Before
	public void setup() {
		payment = null;
	}
	
	public PaymentRepositorySteps() {
		When("^([A-Z][a-z]*) creates a payment in the repository$", (String personFrom) -> {
			payment = createPayment(personFrom, "Someone");
			repository.create(payment);
		});
		
		Then("^She can retrieve it$", () -> {
			JsonNode paymentFromRepository = repository.read(payment.get("id").asText());
			assert(payment.equals(paymentFromRepository));
		});
	}
}
