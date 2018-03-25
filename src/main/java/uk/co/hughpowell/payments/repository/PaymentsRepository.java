package uk.co.hughpowell.payments.repository;

import java.util.Collection;
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
	
	public JsonNode read(String indexId) {
		JsonNode result = repository.get(indexId);
		if (result == null) {
			throw new PaymentNotFound(indexId);
		}
		return result;
	}
	
	public void replace(String indexId, JsonNode payment) {
		if (payment == null) {
			throw new NullPointerException("payment is null");
		}
		String idOfPayment = payment.get("id").asText();
		if (!indexId.equals(idOfPayment)) {
			throw new MismatchedIds(indexId, idOfPayment);
		}
		repository.put(indexId, payment);
	}
	
	public void delete(String indexId) {
		repository.remove(indexId);
	}
	
	public Collection<JsonNode> readPayments() {
		return repository.values();
	}
	
	// TODO: Work out how to get Cucumber to recycle the repository
	public void empty() {
		repository.clear();
	}
}
