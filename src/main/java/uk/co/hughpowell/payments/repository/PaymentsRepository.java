package uk.co.hughpowell.payments.repository;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import org.springframework.stereotype.Component;

@Component
public class PaymentsRepository {
	
	// TODO: Add configuration to make this configurable
	private int timeToWaitForTransfer = 10;
	
	private TransferQueue<RepositoryUpdate> queue = new LinkedTransferQueue<RepositoryUpdate>();
	
	private ConcurrentRepository repository;
	
	public PaymentsRepository() {
		repository = new ConcurrentRepository(queue);
		new Thread(repository).start();
	}
	
	private void queue(RepositoryUpdate update) throws InterruptedException {
		boolean success = queue.tryTransfer(update,
				timeToWaitForTransfer,
				TimeUnit.SECONDS);
		if (!success) {
			throw new UpdateFailed(update);
		}
	}
	
	public void create(Payment payment) throws InterruptedException {
		if (payment == null) {
			throw new NullPointerException("payment must not be null");
		}
		BlockingQueue<UpdateResult> pipe = new ArrayBlockingQueue<UpdateResult>(1);
		queue(new CreateUpdate(payment, pipe));
		if (pipe.take() == UpdateResult.ALREADY_EXISTS) {
			throw new PaymentAlreadyExists(payment.getIndex());
		}
	}
	
	public Payment read(String indexId) {
		Payment result = repository.get(indexId);
		if (result == null) {
			throw new PaymentNotFound(indexId);
		}
		return result;
	}
	
	public void replace(String indexId, String digest, Payment payment) throws InterruptedException {
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
		BlockingQueue<UpdateResult> pipe = new ArrayBlockingQueue<UpdateResult>(1);
		queue(new ReplaceUpdate(indexId, digest, payment, pipe));
		switch(pipe.take()) {
			case MISMATCHED_DIGESTS:
				throw new MismatchedDigests();
			case MISMATCHED_IDS:
				throw new MismatchedIds(indexId, payment.getIndex());
			case DOES_NOT_EXIST:
				throw new PaymentNotFound(indexId);
		}
	}
	
	public void delete(String indexId) throws InterruptedException {
		queue(new RemoveUpdate(indexId));
	}
	
	public Collection<Payment> readPayments() {
		return repository.get();
	}
	
	// TODO: Work out how to get Cucumber to recycle the repository
	public void empty() {
		repository.clear();
	}
}
