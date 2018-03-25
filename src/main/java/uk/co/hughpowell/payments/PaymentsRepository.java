package uk.co.hughpowell.payments;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PaymentsRepository {
	private final Map<String, JsonNode> repository = new HashMap<String, JsonNode>();
	public void create(JsonNode payment) {
		repository.put(payment.get("id").asText(), payment);
	}
	
	public JsonNode read(String paymentId) {
		return repository.get(paymentId);
	}
}
