package uk.co.hughpowell.payments.repository;

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
		JsonNode result = repository.get(paymentId);
		if (result == null) {
			throw new PaymentNotFound(paymentId);
		}
		return result;
	}
	
	public void replace(String paymentId, JsonNode payment) {
		if (payment == null) {
			throw new NullPointerException();
		}
		repository.put(paymentId, payment);
	}
	
	public void delete(String paymentId) {
		repository.remove(paymentId);
	}
}
