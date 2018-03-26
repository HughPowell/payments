package uk.co.hughpowell.payments.orchestrator;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

import com.lambdista.util.Try;

import uk.co.hughpowell.payments.models.CreateEvent;
import uk.co.hughpowell.payments.models.Event;
import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.models.DeleteEvent;
import uk.co.hughpowell.payments.models.ReplaceEvent;
import uk.co.hughpowell.payments.projections.PaymentsReadProjection;
import uk.co.hughpowell.payments.store.PaymentsStore;
import uk.co.hughpowell.payments.validation.CreationValidator;
import uk.co.hughpowell.payments.validation.ReplacementValidator;
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

	public void create(Payment payment) throws Throwable {
		BlockingQueue<Try<Event>> pipe =
				new ArrayBlockingQueue<Try<Event>>(1);
		Event update = new CreateEvent(payment, new CreationValidator(), pipe);
		store.store(update);
		pipe.take().checkedGet();
	}
	
	public Payment read(String indexId) {
		return projection.read(indexId);
	}
	
	public void replace(String indexToBeUpdated, String lastKnownDigest, Payment payment) throws Throwable {
		BlockingQueue<Try<Event>> pipe =
				new ArrayBlockingQueue<Try<Event>>(1);
		Event update = new ReplaceEvent(
				payment,
				new ReplacementValidator(indexToBeUpdated, lastKnownDigest),
				pipe);
		store.store(update);
		pipe.take().checkedGet();
	}
	
	public void delete(String indexId) throws Throwable {
		BlockingQueue<Try<Event>> pipe =
				new ArrayBlockingQueue<Try<Event>>(1);
		Event update = new DeleteEvent(indexId, pipe);
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
