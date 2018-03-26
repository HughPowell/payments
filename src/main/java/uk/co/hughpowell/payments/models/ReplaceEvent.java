package uk.co.hughpowell.payments.models;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

public class ReplaceEvent implements Event {
	
	private final String index;
	private final String digest;
	private final Payment payment;
	private final BlockingQueue<Try<Event>> pipe;
	
	public ReplaceEvent(String index, 
			String digest, 
			Payment payment, 
			BlockingQueue<Try<Event>> pipe) {
		this.index = index;
		this.digest = digest;
		this.payment = payment;
		this.pipe = pipe;
	}

	@Override
	public Try<Event> update(Map<String, Payment> repository) throws InterruptedException {
		Payment currentPayment = repository.get(payment.getIndex());
		if (currentPayment == null) {
			return new Try.Failure<>(new PaymentNotFoundException(payment.getIndex()));
		} else if (!currentPayment.getDigest().equals(digest)) {
			return new Try.Failure<>(new MismatchedDigestsException());
		} else if (!currentPayment.getIndex().equals(index)) {
			return new Try.Failure<>(
					new MismatchedIdsException(currentPayment.getIndex(), index));
		} else {
			repository.put(payment.getIndex(), payment);
			return new Try.Success<Event>(this);
		}
	}

	@Override
	public void complete(Try<Event> result) throws InterruptedException {
		pipe.put(result);
	}
}
