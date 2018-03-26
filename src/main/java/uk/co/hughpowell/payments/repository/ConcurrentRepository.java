package uk.co.hughpowell.payments.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.lambdista.util.Try;

class ConcurrentRepository implements Runnable {

	private final Map<String, Payment> repository = new HashMap<String, Payment>();

	private ImmutableMap<String, Payment> repositoryView = ImmutableMap.copyOf(repository);
	private final BlockingQueue<RepositoryUpdate> queue;
	
	ConcurrentRepository(BlockingQueue<RepositoryUpdate> queue) {
		this.queue = queue;
	}
	
	Payment get(String paymentId) {
		return repositoryView.get(paymentId);
	}
	
	ImmutableCollection<Payment> get() {
		return repositoryView.values();
	}
	
	private void updateView() {
		repositoryView = ImmutableMap.copyOf(repository);
	}

	@Override
	public void run() {
		while (true) {
			try {
				RepositoryUpdate update = queue.take();
				Try<RepositoryUpdate> result = update.update(repository);
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
