package uk.co.hughpowell.payments.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

import uk.co.hughpowell.payments.models.Event;
import uk.co.hughpowell.payments.store.PaymentsStorage;

@Component
public class InMemoryStorageClient implements PaymentsStorage {
	
	private boolean shouldFail = false;
	private List<Event> source = new ArrayList<Event>();

	@Override
	public Event store(Event event) {
		if (shouldFail) {
			throw new RuntimeException("Failed to store event");
		}
		source.add(event);
		return event;
	}

	@Override
	public void publishAllEvents(BlockingQueue<Event> queue) throws InterruptedException {
		for (Event event : source) {
			queue.put(event);
		}
	}

	public void shouldFail() {
		shouldFail = true;
	}
	
	public void shouldSucceed() {
		shouldFail = false;
	}
}
