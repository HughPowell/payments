package uk.co.hughpowell.payments.repository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

class CreateUpdate implements RepositoryUpdate {
	
	private final Payment payment;
	private final BlockingQueue<Try<RepositoryUpdate>> pipe;
	
	CreateUpdate(Payment payment, BlockingQueue<Try<RepositoryUpdate>> pipe) {
		this.payment = payment;
		this.pipe = pipe;
	}

	@Override
	public Try<RepositoryUpdate> update(Map<String, Payment> repository) throws InterruptedException {
		if (repository.containsKey(payment.getIndex())) {
			return new Try.Failure<>(new PaymentAlreadyExists(payment.getIndex()));
		} else {
			repository.put(payment.getIndex(), payment);
			return new Try.Success<>(this);
		}
	}
	
	@Override
	public void complete(Try<RepositoryUpdate> result) throws InterruptedException {
		pipe.put(result);
	}

}
