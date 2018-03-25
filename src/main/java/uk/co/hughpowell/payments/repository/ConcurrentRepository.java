package uk.co.hughpowell.payments.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

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
	
	private void updateView(Map<String, Payment> repository) {
		repositoryView = ImmutableMap.copyOf(repository);
	}

	@Override
	public void run() {
		while (true) {
			try {
				RepositoryUpdate update = queue.peek();
				if (update != null) {
					updateView(update.update(repository));
					queue.take();
				} else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	// TODO: Work out how to get Cucumber to recycle the repository
	public void clear() {
		repository.clear();
		updateView(repository);
	}
}
