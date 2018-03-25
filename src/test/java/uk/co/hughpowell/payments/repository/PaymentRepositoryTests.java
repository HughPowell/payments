package uk.co.hughpowell.payments.repository;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

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
	public void shouldGetAPaymentWhenOneIsCreated() {
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
		
		JsonNode updatedPayment = Payment.updateAmount(payment, 200);
		repository.replace(updatedPayment);
		
		JsonNode retrievedPayment = repository.read(updatedPayment.get("id").asText());
		assertEquals(updatedPayment, retrievedPayment);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenReplacingWithANullPayment() {
		repository.create(Payment.create("Alice", "Bob"));
		
		repository.replace(null);
	}
}
