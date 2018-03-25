package uk.co.hughpowell.payments;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Payment {

	final static JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
	
	public static JsonNode create(String from, String to, int amount) {
		Map<String, JsonNode> paymentProperties = new HashMap<String, JsonNode>();
		JsonNode jsonUUID = jsonFactory.textNode(UUID.randomUUID().toString());
		paymentProperties.put("id", jsonUUID);
		paymentProperties.put("from", jsonFactory.textNode(from));
		paymentProperties.put("to", jsonFactory.textNode(to));
		paymentProperties.put("amount", jsonFactory.numberNode(amount));
		return jsonFactory.objectNode().setAll(paymentProperties);
	}
	
	public static JsonNode create(String from, String to) {
		return create(from, to, 100);
	}
	
	public static JsonNode updateAmount(JsonNode payment, int amount) {
		return ((ObjectNode) payment).set("amount", jsonFactory.numberNode(amount));
	}
	
}
