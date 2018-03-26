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

import uk.co.hughpowell.payments.PaymentUtils;
import uk.co.hughpowell.payments.PaymentsApplication;
import uk.co.hughpowell.payments.models.MismatchedDigestsException;
import uk.co.hughpowell.payments.models.MismatchedIdsException;
import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.models.PaymentAlreadyExistsException;
import uk.co.hughpowell.payments.models.PaymentNotFoundException;
import uk.co.hughpowell.payments.orchestrator.NullDigestException;
import uk.co.hughpowell.payments.orchestrator.PaymentsOrchestrator;
import uk.co.hughpowell.payments.store.InMemoryStorage;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentsApplication.class)
public class PaymentOrchestratorTests {
	private PaymentsOrchestrator orchestrator;
	
	@Before
	public void setup() throws InterruptedException {
		orchestrator = new PaymentsOrchestrator(new InMemoryStorage());
	}

	@Test
	public void shouldBeAbleToGetAPaymentWhenOneIsCreated()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		
		Payment retrievedPayment = orchestrator.read(payment.getIndex());
		
		assertEquals(payment, retrievedPayment);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenCreatingANullPayment()
			throws Throwable {
		orchestrator.create(null);
	}
	
	@Test(expected = PaymentAlreadyExistsException.class)
	public void shouldThrowExceptionWhenCreatingAPaymentThatAlreadyExists()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		orchestrator.create(payment);
	}
	
	@Test(expected = PaymentNotFoundException.class)
	public void shouldThrowExceptionWhenGettingNonExistantPayment() {
		orchestrator.read(UUID.randomUUID().toString());
	}
	
	@Test
	public void shouldReplaceTheExistingPaymentWithTheOneGiven()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob", 100));
		orchestrator.create(payment);
		
		JsonNode updatedPayment = PaymentUtils.updateAmount(payment.getData(), 200);
		orchestrator.replace(payment.getIndex(),
				payment.getDigest(),
				new Payment(updatedPayment));
		
		Payment retrievedPayment = orchestrator.read(payment.getIndex());
		assertEquals(updatedPayment, retrievedPayment.getData());
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenReplacingWithANullPayment()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		
		orchestrator.replace(payment.getIndex(), payment.getDigest(), null);
	}
	
	@Test(expected = NullDigestException.class)
	public void shouldThrowExceptionWhenNoDigestIsProvided()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.replace(payment.getIndex(), null, payment);
	}
	
	@Test(expected = MismatchedIdsException.class)
	public void shouldThrowExceptionWhenGivenPaymentIdAndIdOfPaymentAreMismatched()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		String differentPaymentId = UUID.randomUUID().toString();
		
		orchestrator.replace(differentPaymentId, payment.getDigest(), payment);
	}
	
	@Test(expected = MismatchedDigestsException.class)
	public void shouldThrowExceptionWhenGivenDigestDoesNotMatchDigestOfCurrentPayment()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		Payment updatedPayment = 
				new Payment(PaymentUtils.updateAmount(payment.getData(), 200));
		orchestrator.replace(updatedPayment.getIndex(), "SomeOtherDigest", updatedPayment);
	}
	
	@Test(expected = PaymentNotFoundException.class)
	public void shouldThrowExceptionWhenUpdatingANonExistantPayment()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
				
		orchestrator.replace(payment.getIndex(), payment.getDigest(), payment);
	}
	
	@Test(expected = PaymentNotFoundException.class)
	public void shouldDeleteThePaymentAssociatedWithTheGivenId()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		
		orchestrator.delete(payment.getIndex());
		
		orchestrator.read(payment.getIndex());
	}
	
	@Test
	public void shouldReturnAnEmptyCollectionWhenThereAreNoPayments() {
		Collection<Payment> payments = orchestrator.readPayments();
		assert(payments.isEmpty());
	}
	
	@Test
	public void shouldReturnAListOfAllPayments() throws Throwable {
		orchestrator.create(new Payment(PaymentUtils.create("Alice", "Bob", 100)));
		orchestrator.create(new Payment(PaymentUtils.create("Alice", "Bob", 200)));
		orchestrator.create(new Payment(PaymentUtils.create("Alice", "Bob", 300)));
		
		Collection<Payment> payments = orchestrator.readPayments();
		
		Set<String> ids = payments
				.stream()
				.map(p -> p.getIndex())
				.collect(Collectors.toSet());
		assertEquals(ids.size(), 3);
	}
}
