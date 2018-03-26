package uk.co.hughpowell.payments.repository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.lambdista.util.Try;

class RemoveUpdate implements RepositoryUpdate {

	private final String paymentId;
	private final BlockingQueue<Try<RepositoryUpdate>> pipe;
	
	RemoveUpdate(String paymentId, BlockingQueue<Try<RepositoryUpdate>> pipe) {
		this.paymentId = paymentId;
		this.pipe = pipe;
	}
	
	@Override
	public Try<RepositoryUpdate> update(Map<String, Payment> repository) {
		repository.remove(paymentId);
		return new Try.Success<RepositoryUpdate>(this);
	}
	
	@Override
	public void complete(Try<RepositoryUpdate> result) throws InterruptedException {
		pipe.put(result);
	}
}
