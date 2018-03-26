package uk.co.hughpowell.payments.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.orchestrator.PaymentsOrchestrator;

@RestController
@RequestMapping("/payments")
public class PaymentsRestController {
	
	private final PaymentsOrchestrator orchestrator;
	
	PaymentsRestController(PaymentsOrchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> createPayment(@RequestBody JsonNode payment)
			throws Throwable {
		Payment storedPayment = new Payment(payment);
		orchestrator.create(new Payment(payment));
		Link linkToPayment = new PaymentResource(payment).getLink("self");
		URI uriToPayment = URI.create(linkToPayment.getHref());
		return ResponseEntity
				.created(uriToPayment)
				.eTag("\"" + storedPayment.getDigest() + "\"")
				.build();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	Resources<PaymentResource> readPayments() {
		List<PaymentResource> paymentResources = orchestrator
				.readPayments()
				.stream()
				.map(storedPayment -> new PaymentResource(storedPayment.getData()))
				.collect(Collectors.toList());
		return new Resources<>(paymentResources);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{paymentId}")
	ResponseEntity<PaymentResource> readPayment(@PathVariable String paymentId) {
		Payment payment = orchestrator.read(paymentId);
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"" + payment.getDigest() + "\"");
		return new ResponseEntity<PaymentResource>(new PaymentResource(payment.getData()), headers, HttpStatus.OK);
	}
	
	private static String stripQuotes(String digest) {
		return digest.substring(1, digest.length() - 1);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{paymentId}")
	ResponseEntity<?> replacePayment(
			@PathVariable String paymentId,
			@RequestBody JsonNode payment,
			@RequestHeader("If-Match") String digest) throws Throwable {
		Payment storedPayment = new Payment(payment);
		digest = stripQuotes(digest);
		orchestrator.replace(paymentId, digest, storedPayment);
		return ResponseEntity
				.noContent()
				.eTag("\"" + storedPayment.getDigest() + "\"")
				.build();
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{paymentId}")
	ResponseEntity<?> deletePayment(@PathVariable String paymentId)
			throws Throwable {
		orchestrator.delete(paymentId);
		return ResponseEntity.noContent().build();
	}
}
