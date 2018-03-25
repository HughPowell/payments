package uk.co.hughpowell.payments;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.databind.JsonNode;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class PaymentResource extends ResourceSupport {
	private final JsonNode payment;
	
	public PaymentResource(JsonNode payment) {
		this.payment = payment;
		this.add(linkTo(PaymentsRestController.class).withRel("payments"));
		this.add(linkTo(methodOn(PaymentsRestController.class)
				.getPayment(payment.get("id").asText())).withSelfRel());
	}
	
	public JsonNode getPayment() {
		return payment;
	}
}
