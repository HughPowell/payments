package uk.co.hughpowell.payments;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentsApplication.class)
public class PaymentRepositoryTests {
	final JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
	
	private JsonNode createPayment(String from, String to) {
		Map<String, JsonNode> paymentProperties = new HashMap<String, JsonNode>();
		JsonNode jsonUUID = jsonFactory.textNode(UUID.randomUUID().toString());
		paymentProperties.put("id", jsonUUID);
		paymentProperties.put("from", jsonFactory.textNode(from));
		paymentProperties.put("to", jsonFactory.textNode(to));
		return jsonFactory.objectNode().setAll(paymentProperties);
	}
	
	private PaymentsRepository repository;
	
	@Before
	public void setup() {
		repository = new PaymentsRepository();
	}

	@Test
	public void shouldGetAPaymentWhenOneIsCreated() {
		JsonNode payment = createPayment("Alice", "Bob");
		repository.create(payment);
		
		JsonNode retrievedPayment = repository.read(payment.get("id").asText());
		
		assertEquals(payment, retrievedPayment);
	}
	
	@Test(expected = PaymentNotFound.class)
	public void shouldThrowExceptionWhenGettingNonExistantPayment() {
		repository.read(UUID.randomUUID().toString());
	}
}
