package uk.co.hughpowell.payments.repository;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;

import uk.co.hughpowell.payments.PaymentUtils;
import uk.co.hughpowell.payments.PaymentsApplication;
import uk.co.hughpowell.payments.clients.InMemoryStorageClient;
import uk.co.hughpowell.payments.models.CreateEvent;
import uk.co.hughpowell.payments.models.DeleteEvent;
import uk.co.hughpowell.payments.models.Event;
import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.models.ReplaceEvent;
import uk.co.hughpowell.payments.orchestrator.PaymentsOrchestrator;
import uk.co.hughpowell.payments.validation.MismatchedDigestsException;
import uk.co.hughpowell.payments.validation.MismatchedIdsException;
import uk.co.hughpowell.payments.validation.NullDigestException;
import uk.co.hughpowell.payments.validation.PaymentAlreadyExistsException;
import uk.co.hughpowell.payments.validation.PaymentNotFoundException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentsApplication.class)
public class PaymentOrchestratorTests {
	private PaymentsOrchestrator orchestrator;
	private InMemoryStorageClient storageClient; 
	
	@Before
	public void setup() throws InterruptedException {
		storageClient = new InMemoryStorageClient();
		orchestrator = new PaymentsOrchestrator(storageClient);
	}

	@Test
	public void shouldBeAbleToGetAPaymentWhenOneIsCreated()
			throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		
		Payment retrievedPayment = orchestrator.read(payment.getIndex());
		
		assertEquals(payment, retrievedPayment);
	}
	
	@Test
	public void shouldStoreEventWhenCreatedEventReceived() throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		
		BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(5);
		storageClient.publishAllEvents(queue);
		
		assertEquals(CreateEvent.class, queue.take().getClass());
		assert(queue.isEmpty());
		
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
	
	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenWritingCreatedEventToStorageFails()
			throws Throwable {
		storageClient.shouldFail();
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
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

	@Test
	public void shouldStoreEventWhenReplacementEventReceived() throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		orchestrator.replace(payment.getIndex(), payment.getDigest(), payment);
		
		BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(5);
		storageClient.publishAllEvents(queue);
		
		assertEquals(CreateEvent.class, queue.take().getClass());
		assertEquals(ReplaceEvent.class, queue.take().getClass());
		assert(queue.isEmpty());
		
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

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenWritingReplacementEventToStorageFails()
			throws Throwable {
		storageClient.shouldFail();
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));

		orchestrator.replace("SomeIndex", "SomeDigest", payment);
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
	public void shouldStoreEventWhenDeletionEventReceived() throws Throwable {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		orchestrator.create(payment);
		orchestrator.delete(payment.getIndex());
		
		BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(5);
		storageClient.publishAllEvents(queue);
		
		assertEquals(CreateEvent.class, queue.take().getClass());
		assertEquals(DeleteEvent.class, queue.take().getClass());
		assert(queue.isEmpty());
		
	}
	
	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenWritingDeletionEventToStorageFails()
			throws Throwable {
		storageClient.shouldFail();

		orchestrator.delete("SomeIndex");
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
