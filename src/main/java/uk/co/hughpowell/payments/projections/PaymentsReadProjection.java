package uk.co.hughpowell.payments.projections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.lambdista.util.Try;

import uk.co.hughpowell.payments.models.Event;
import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.validation.PaymentNotFoundException;

public class PaymentsReadProjection implements Runnable {

	private final Map<String, Payment> repository = new HashMap<String, Payment>();

	private ImmutableMap<String, Payment> repositoryView = ImmutableMap.copyOf(repository);
	private final BlockingQueue<Event> queue;
	
	public PaymentsReadProjection(BlockingQueue<Event> queue) {
		this.queue = queue;
	}
	
	public Payment read(String indexId) {
		Payment result = repositoryView.get(indexId);
		if (result == null) {
			throw new PaymentNotFoundException(indexId);
		}
		return result;
	}
	
	public ImmutableCollection<Payment> readPayments() {
		return repositoryView.values();
	}
	
	private void updateView() {
		repositoryView = ImmutableMap.copyOf(repository);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Event update = queue.take();
				Try<Event> result = update.update(repository);
				updateView();
				update.complete(result);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	// TODO: Work out how to get Cucumber to recycle the repository
	public void clear() {
		repository.clear();
		updateView();
	}
}
