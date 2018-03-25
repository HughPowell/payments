package uk.co.hughpowell.payments;

import java.net.URI;

import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;

@RestController
@RequestMapping("/payments")
public class PaymentsRestController {
	
	private final JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> createPayment(@RequestBody JsonNode payment) {
		Link linkToPayment = new PaymentResource(payment).getLink("self");
		URI uriToPayment = URI.create(linkToPayment.getHref());
		return ResponseEntity.created(uriToPayment).build();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{paymentId}")
	PaymentResource getPayment(@PathVariable String paymentId) {
		TextNode jsonPaymentId = jsonFactory.textNode(paymentId);
		return new PaymentResource(jsonFactory.objectNode().set("id", jsonPaymentId));
	}
}
