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
	public void shouldBeAbleToGetAPaymentWhenOneIsCreated()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		repository.create(payment);
		
		Payment retrievedPayment = repository.read(payment.getIndex());
		
		assertEquals(payment, retrievedPayment);
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenCreatingANullPayment()
			throws InterruptedException {
		repository.create(null);
	}
	
	@Test(expected = PaymentAlreadyExists.class)
	public void shouldThrowExceptionWhenCreatingAPaymentThatAlreadyExists()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		repository.create(payment);
		repository.create(payment);
	}
	
	@Test(expected = PaymentNotFound.class)
	public void shouldThrowExceptionWhenGettingNonExistantPayment() {
		repository.read(UUID.randomUUID().toString());
	}
	
	@Test
	public void shouldReplaceTheExistingPaymentWithTheOneGiven()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob", 100));
		repository.create(payment);
		
		JsonNode updatedPayment = PaymentUtils.updateAmount(payment.getData(), 200);
		repository.replace(payment.getIndex(),
				payment.getDigest(),
				new Payment(updatedPayment));
		
		Payment retrievedPayment = repository.read(payment.getIndex());
		assertEquals(updatedPayment, retrievedPayment.getData());
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenReplacingWithANullPayment()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		repository.create(payment);
		
		repository.replace(payment.getIndex(), payment.getDigest(), null);
	}
	
	@Test(expected = NullDigest.class)
	public void shouldThrowExceptionWhenNoDigestIsProvided()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		repository.replace(payment.getIndex(), null, payment);
	}
	
	@Test(expected = MismatchedIds.class)
	public void shouldThrowExceptionWhenGivenPaymentIdAndIdOfPaymentAreMismatched()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		String differentPaymentId = UUID.randomUUID().toString();
		
		repository.replace(differentPaymentId, payment.getDigest(), payment);
	}
	
	@Test(expected = MismatchedDigests.class)
	public void shouldThrowExceptionWhenGivenDigestDoesNotMatchDigestOfCurrentPayment()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		repository.create(payment);
		Payment updatedPayment = 
				new Payment(PaymentUtils.updateAmount(payment.getData(), 200));
		repository.replace(updatedPayment.getIndex(), "SomeOtherDigest", updatedPayment);
	}
	
	@Test(expected = PaymentNotFound.class)
	public void shouldThrowExceptionWhenUpdatingANonExistantPayment()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
				
		repository.replace(payment.getIndex(), payment.getDigest(), payment);
	}
	
	@Test(expected = PaymentNotFound.class)
	public void shouldDeleteThePaymentAssociatedWithTheGivenId()
			throws InterruptedException {
		Payment payment = new Payment(PaymentUtils.create("Alice", "Bob"));
		repository.create(payment);
		
		repository.delete(payment.getIndex());
		
		repository.read(payment.getIndex());
	}
	
	@Test
	public void shouldReturnAnEmptyCollectionWhenThereAreNoPayments() {
		Collection<Payment> payments = repository.readPayments();
		assert(payments.isEmpty());
	}
	
	@Test
	public void shouldReturnAListOfAllPayments() throws InterruptedException {
		repository.create(new Payment(PaymentUtils.create("Alice", "Bob", 100)));
		repository.create(new Payment(PaymentUtils.create("Alice", "Bob", 200)));
		repository.create(new Payment(PaymentUtils.create("Alice", "Bob", 300)));
		
		Collection<Payment> payments = repository.readPayments();
		
		Set<String> ids = payments
				.stream()
				.map(p -> p.getIndex())
				.collect(Collectors.toSet());
		assertEquals(ids.size(), 3);
	}
}
