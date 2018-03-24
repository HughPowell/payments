package uk.co.hughpowell.payments;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/payments")
public class PaymentsRestController {

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> createPayment(@RequestBody JsonNode payment) {
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(payment.get("id"))
				.toUri();
		return ResponseEntity.created(location).build();
	}
}
