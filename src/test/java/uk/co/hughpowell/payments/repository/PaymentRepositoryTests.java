package uk.co.hughpowell.payments.repository;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;

import uk.co.hughpowell.payments.Payment;
import uk.co.hughpowell.payments.PaymentsApplication;
import uk.co.hughpowell.payments.repository.PaymentNotFound;
import uk.co.hughpowell.payments.repository.PaymentsRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentsApplication.class)
public class PaymentRepositoryTests {
	private PaymentsRepository repository;
	
	@Before
	public void setup() {
		repository = new PaymentsRepository();
	}

	@Test
	public void shouldBeAbleToGetAPaymentWhenOneIsCreated() {
		JsonNode payment = Payment.create("Alice", "Bob");
		repository.create(payment);
		
		JsonNode retrievedPayment = repository.read(payment.get("id").asText());
		
		assertEquals(payment, retrievedPayment);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenCreatingANullPayment() {
		repository.create(null);
	}
	
	@Test(expected = PaymentNotFound.class)
	public void shouldThrowExceptionWhenGettingNonExistantPayment() {
		repository.read(UUID.randomUUID().toString());
	}
	
	@Test
	public void shouldReplaceTheExistingPaymentWithTheOneGiven() {
		JsonNode payment = Payment.create("Alice", "Bob", 100);
		repository.create(payment);
		
		String paymentId = payment.get("id").asText();
		JsonNode updatedPayment = Payment.updateAmount(payment, 200);
		repository.replace(paymentId, updatedPayment);
		
		JsonNode retrievedPayment = repository.read(updatedPayment.get("id").asText());
		assertEquals(updatedPayment, retrievedPayment);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenReplacingWithANullPayment() {
		JsonNode payment = Payment.create("Alice", "Bob");
		repository.create(payment);
		
		String paymentId = payment.get("id").asText();
		repository.replace(paymentId, null);
	}
	
	@Test(expected = MismatchedIds.class)
	public void shouldThrowExceptionWhenGivenPaymentIdAndIdOfPaymentAreMismatched() {
		JsonNode payment = Payment.create("Alice", "Bob");
		String differentPaymentId = UUID.randomUUID().toString();
		
		repository.replace(differentPaymentId, payment);
	}
	
	@Test(expected = PaymentNotFound.class)
	public void shouldDeleteThePaymentAssociatedWithTheGivenId() {
		JsonNode payment = Payment.create("Alice", "Bob");
		String paymentId = payment.get("id").asText();
		repository.create(payment);
		
		repository.delete(paymentId);
		
		repository.read(paymentId);
	}
	
	@Test
	public void shouldReturnAnEmptyCollectionWhenThereAreNoPayments() {
		Collection<JsonNode> payments = repository.readPayments();
		assert(payments.isEmpty());
	}
	
	@Test
	public void shouldReturnAListOfAllPayments() {
		repository.create(Payment.create("Alice", "Bob", 100));
		repository.create(Payment.create("Alice", "Bob", 200));
		repository.create(Payment.create("Alice", "Bob", 300));
		
		Collection<JsonNode> payments = repository.readPayments();
		
		Set<String> ids = payments
				.stream()
				.map(p -> p.get("id").asText())
				.collect(Collectors.toSet());
		assertEquals(ids.size(), 3);
	}
}
