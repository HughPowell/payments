package uk.co.hughpowell.payments.models;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

public class RemoveEvent implements Event {

	private final String paymentId;
	private final BlockingQueue<Try<Event>> pipe;
	
	public RemoveEvent(String paymentId, BlockingQueue<Try<Event>> pipe) {
		this.paymentId = paymentId;
		this.pipe = pipe;
	}
	
	@Override
	public Try<Event> update(Map<String, Payment> repository) {
		repository.remove(paymentId);
		return new Try.Success<Event>(this);
	}
	
	@Override
	public void complete(Try<Event> result) throws InterruptedException {
		pipe.put(result);
	}
}
