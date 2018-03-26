package uk.co.hughpowell.payments.orchestrator;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

import com.lambdista.util.Try;

import uk.co.hughpowell.payments.models.CreateEvent;
import uk.co.hughpowell.payments.models.Event;
import uk.co.hughpowell.payments.models.MismatchedIdsException;
import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.models.RemoveEvent;
import uk.co.hughpowell.payments.models.ReplaceEvent;
import uk.co.hughpowell.payments.projections.PaymentsReadProjection;
import uk.co.hughpowell.payments.store.PaymentsStore;
import uk.co.hughpowell.payments.store.PaymentsStorage;

@Component
public class PaymentsOrchestrator {
	
	private final PaymentsStore store;
	private final PaymentsReadProjection projection;
	private final BlockingQueue<Event> input =
			new ArrayBlockingQueue<Event>(1024);
	
	public PaymentsOrchestrator(PaymentsStorage store) throws InterruptedException {
		this.store = new PaymentsStore(store, input);
		this.projection = new PaymentsReadProjection(input);
		new Thread(projection).start();
	}

	public void validateCreation(Payment payment) {
		if (payment == null) {
			throw new NullPointerException("payment must not be null");
		}
	}

	public void validateReplacement(String indexId, String digest, Payment payment) {
		if (payment == null) {
			throw new NullPointerException("payment is null");
		}
		if (digest == null) {
			throw new NullDigestException();
		}
		String idOfPayment = payment.getIndex();
		if (!indexId.equals(idOfPayment)) {
		   throw new MismatchedIdsException(indexId, idOfPayment);
	   	}
	}

	public void create(Payment payment) throws Throwable {
		validateCreation(payment);
		BlockingQueue<Try<Event>> pipe =
				new ArrayBlockingQueue<Try<Event>>(1);
		Event update = new CreateEvent(payment, pipe);
		store.store(update);
		pipe.take().checkedGet();
	}
	
	public Payment read(String indexId) {
		return projection.read(indexId);
	}
	
	public void replace(String indexId, String digest, Payment payment) throws Throwable {
		validateReplacement(indexId, digest, payment);
		BlockingQueue<Try<Event>> pipe =
				new ArrayBlockingQueue<Try<Event>>(1);
		Event update = new ReplaceEvent(indexId, digest, payment, pipe);
		store.store(update);
		pipe.take().checkedGet();
	}
	
	public void delete(String indexId) throws Throwable {
		BlockingQueue<Try<Event>> pipe =
				new ArrayBlockingQueue<Try<Event>>(1);
		Event update = new RemoveEvent(indexId, pipe);
		store.store(update);
		pipe.take().checkedGet();
	}
	
	public Collection<Payment> readPayments() {
		return projection.readPayments();
	}
	
	public void clear() {
		projection.clear();
	}
}
