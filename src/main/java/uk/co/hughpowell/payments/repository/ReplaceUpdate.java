package uk.co.hughpowell.payments.repository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

class ReplaceUpdate implements RepositoryUpdate {
	
	private final String index;
	private final String digest;
	private final Payment payment;
	private final BlockingQueue<Try<RepositoryUpdate>> pipe;
	
	ReplaceUpdate(String index, 
			String digest, 
			Payment payment, 
			BlockingQueue<Try<RepositoryUpdate>> pipe) {
		this.index = index;
		this.digest = digest;
		this.payment = payment;
		this.pipe = pipe;
	}

	@Override
	public Try<RepositoryUpdate> update(Map<String, Payment> repository) throws InterruptedException {
		Payment currentPayment = repository.get(payment.getIndex());
		if (currentPayment == null) {
			return new Try.Failure<>(new PaymentNotFound(payment.getIndex()));
		} else if (!currentPayment.getDigest().equals(digest)) {
			return new Try.Failure<>(new MismatchedDigests());
		} else if (!currentPayment.getIndex().equals(index)) {
			return new Try.Failure<>(
					new MismatchedIds(currentPayment.getIndex(), index));
		} else {
			repository.put(payment.getIndex(), payment);
			return new Try.Success<RepositoryUpdate>(this);
		}
	}

	@Override
	public void complete(Try<RepositoryUpdate> result) throws InterruptedException {
		pipe.put(result);
	}
}
