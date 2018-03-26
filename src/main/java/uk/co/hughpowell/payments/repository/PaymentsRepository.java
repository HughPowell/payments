package uk.co.hughpowell.payments.repository;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.lambdista.util.Try;

@Component
public class PaymentsRepository {
	
	// TODO: Add configuration to make this configurable
	private int timeToWaitForTransfer = 10;
	
	private BlockingQueue<RepositoryUpdate> queue = new ArrayBlockingQueue<RepositoryUpdate>(1024);
	
	private ConcurrentRepository repository;
	
	public PaymentsRepository() {
		repository = new ConcurrentRepository(queue);
		new Thread(repository).start();
	}
	
	private void queue(RepositoryUpdate update,
			BlockingQueue<Try<RepositoryUpdate>> pipe)
					throws Throwable {
		boolean success = queue.offer(update,
				timeToWaitForTransfer,
				TimeUnit.SECONDS);
		if (!success) {
			throw new UpdateFailed(update);
		}
		Try<RepositoryUpdate> result = pipe.poll(timeToWaitForTransfer, TimeUnit.SECONDS);
		result.checkedGet();
	}
	
	public void create(Payment payment) throws Throwable {
		if (payment == null) {
			throw new NullPointerException("payment must not be null");
		}
		BlockingQueue<Try<RepositoryUpdate>> pipe =
				new ArrayBlockingQueue<Try<RepositoryUpdate>>(1);
		queue(new CreateUpdate(payment, pipe), pipe);
	}
	
	public Payment read(String indexId) {
		Payment result = repository.get(indexId);
		if (result == null) {
			throw new PaymentNotFound(indexId);
		}
		return result;
	}
	
	public void replace(String indexId, String digest, Payment payment) throws Throwable {
		if (payment == null) {
			throw new NullPointerException("payment is null");
		}
		if (digest == null) {
			throw new NullDigest();
		}
		String idOfPayment = payment.getIndex();
		if (!indexId.equals(idOfPayment)) {
		   throw new MismatchedIds(indexId, idOfPayment);
	   	}
		BlockingQueue<Try<RepositoryUpdate>> pipe =
				new ArrayBlockingQueue<Try<RepositoryUpdate>>(1);
		queue(new ReplaceUpdate(indexId, digest, payment, pipe), pipe);
	}
	
	public void delete(String indexId) throws Throwable {
		BlockingQueue<Try<RepositoryUpdate>> pipe =
				new ArrayBlockingQueue<Try<RepositoryUpdate>>(1);
		queue(new RemoveUpdate(indexId, pipe), pipe);
	}
	
	public Collection<Payment> readPayments() {
		return repository.get();
	}
	
	// TODO: Work out how to get Cucumber to recycle the repository
	public void empty() {
		repository.clear();
	}
}
