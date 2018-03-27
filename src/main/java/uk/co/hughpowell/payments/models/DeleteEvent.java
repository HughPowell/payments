package uk.co.hughpowell.payments.models;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

public class DeleteEvent implements Event {

	private final String paymentId;
	private final BlockingQueue<Try<Event>> pipe;
	
	public DeleteEvent(String paymentId, BlockingQueue<Try<Event>> pipe) {
		this.paymentId = paymentId;
		this.pipe = pipe;
	}
	
	@Override
	public Try<Event> update(Map<String, Payment> map) {
		map.remove(paymentId);
		return new Try.Success<Event>(this);
	}
	
	@Override
	public void complete(Try<Event> result) throws InterruptedException {
		pipe.put(result);
	}
}
