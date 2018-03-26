package uk.co.hughpowell.payments.models;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

public class CreateEvent implements Event {
	
	private final Payment payment;
	private final BlockingQueue<Try<Event>> pipe;
	private final Validator validator;
	
	public CreateEvent(Payment payment, Validator validator, BlockingQueue<Try<Event>> pipe) {
		this.payment = payment;
		this.pipe = pipe;
		this.validator = validator;
		validator.constructionValidation(payment);
	}

	@Override
	public Try<Event> update(Map<String, Payment> map) throws InterruptedException {
		return Try.apply(() -> validator.preInsertionValidation(map, payment))
				.map(payment -> {
					map.put(payment.getIndex(), payment);
					return this;
				});
	}
	
	@Override
	public void complete(Try<Event> result) throws InterruptedException {
		pipe.put(result);
	}

}
