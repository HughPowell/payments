package uk.co.hughpowell.payments.models;

import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class Payment {

	private JsonNode payment;
	
	public Payment(JsonNode payment) {
		this.payment = payment;
	}
	
	public JsonNode getData() {
		return this.payment;
	}
	
	public String getIndex() {
		return this.payment.get("id").asText();
	}
	
	public String getDigest() {
		return DigestUtils.md5DigestAsHex(payment.toString().getBytes());
	}
}
