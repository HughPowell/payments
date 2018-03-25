package uk.co.hughpowell.payments.repository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

class ReplaceUpdate implements RepositoryUpdate {
	
	private final Payment payment;
	private final String digest;
	private final BlockingQueue<UpdateResult> pipe;
	
	ReplaceUpdate(Payment payment, String digest, BlockingQueue<UpdateResult> pipe) {
		this.payment = payment;
		this.digest = digest;
		this.pipe = pipe;
	}

	@Override
	public Map<String, Payment> update(Map<String, Payment> repository) throws InterruptedException {
		Payment currentPayment = repository.get(payment.getIndex());
		if (!currentPayment.getDigest().equals(digest)) {
			pipe.put(UpdateResult.MISMATCHED_DIGESTS);
		} else {
			repository.put(payment.getIndex(), payment);
			pipe.put(UpdateResult.SUCCESS);
		}
		return repository;
	}
}
