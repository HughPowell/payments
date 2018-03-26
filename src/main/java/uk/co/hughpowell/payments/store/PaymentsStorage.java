package uk.co.hughpowell.payments.store;

import java.util.concurrent.BlockingQueue;

import uk.co.hughpowell.payments.models.Event;

public interface PaymentsStorage {
	public Event store(Event event);
	public void publishAllEvents(BlockingQueue<Event> queue) throws InterruptedException;
}
