package uk.co.hughpowell.payments.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import uk.co.hughpowell.payments.repository.PaymentsRepository;

@RestController
@RequestMapping("/payments")
public class PaymentsRestController {
	
	private final PaymentsRepository repository;
	
	PaymentsRestController(PaymentsRepository paymentsRepository) {
		this.repository = paymentsRepository;
	}

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> createPayment(@RequestBody JsonNode payment) {
		repository.create(payment);
		Link linkToPayment = new PaymentResource(payment).getLink("self");
		URI uriToPayment = URI.create(linkToPayment.getHref());
		return ResponseEntity.created(uriToPayment).build();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	Resources<PaymentResource> readPayments() {
		List<PaymentResource> paymentResources = repository
				.readPayments()
				.stream()
				.map(PaymentResource::new)
				.collect(Collectors.toList());
		return new Resources<>(paymentResources);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{paymentId}")
	PaymentResource readPayment(@PathVariable String paymentId) {
		JsonNode payment = repository.read(paymentId);
		return new PaymentResource(payment);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{paymentId}")
	ResponseEntity<?> replacePayment(
			@PathVariable String paymentId,
			@RequestBody JsonNode payment) {
		repository.replace(paymentId, payment);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{paymentId}")
	ResponseEntity<?> deletePayment(@PathVariable String paymentId) {
		repository.delete(paymentId);
		return ResponseEntity.noContent().build();
	}
}
