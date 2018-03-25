package uk.co.hughpowell.payments.repository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

class CreateUpdate implements RepositoryUpdate {
	
	private final Payment payment;
	private final BlockingQueue<UpdateResult> pipe;
	
	CreateUpdate(Payment payment, BlockingQueue<UpdateResult> pipe) {
		this.payment = payment;
		this.pipe = pipe;
	}

	@Override
	public Map<String, Payment> update(Map<String, Payment> repository) throws InterruptedException {
		if (repository.containsKey(payment.getIndex())) {
			pipe.put(UpdateResult.ALREADY_EXISTS);
		} else {
			repository.put(payment.getIndex(), payment);
			pipe.put(UpdateResult.SUCCESS);
		}
		return repository;
	}

}
