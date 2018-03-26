package uk.co.hughpowell.payments.store;

import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

import uk.co.hughpowell.payments.models.Event;

public class PaymentsStore {

	private final PaymentsStorage storage;
	private final BlockingQueue<Event> queue;
	
	public PaymentsStore(PaymentsStorage storage, BlockingQueue<Event> queue) throws InterruptedException {
		this.storage = storage;
		this.queue = queue;
		storage.publishAllEvents(queue);
	}
	
	public void store(Event event) throws InterruptedException {
		try {
			queue.put(storage.store(event));
		} catch(Exception e) {
			event.complete(new Try.Failure<>(e));
		}
	}
}
