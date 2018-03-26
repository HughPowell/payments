package uk.co.hughpowell.payments.models;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

public class CreateEvent implements Event {
	
	private final Payment payment;
	private final BlockingQueue<Try<Event>> pipe;
	
	public CreateEvent(Payment payment, BlockingQueue<Try<Event>> pipe) {
		this.payment = payment;
		this.pipe = pipe;
	}

	@Override
	public Try<Event> update(Map<String, Payment> repository) throws InterruptedException {
		if (repository.containsKey(payment.getIndex())) {
			return new Try.Failure<>(new PaymentAlreadyExistsException(payment.getIndex()));
		} else {
			repository.put(payment.getIndex(), payment);
			return new Try.Success<>(this);
		}
	}
	
	@Override
	public void complete(Try<Event> result) throws InterruptedException {
		pipe.put(result);
	}

}
