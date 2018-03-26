package uk.co.hughpowell.payments.repository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

class ReplaceUpdate implements RepositoryUpdate {
	
	private final String index;
	private final String digest;
	private final Payment payment;
	private final BlockingQueue<UpdateResult> pipe;
	
	ReplaceUpdate(String index, String digest, Payment payment, BlockingQueue<UpdateResult> pipe) {
		this.index = index;
		this.digest = digest;
		this.payment = payment;
		this.pipe = pipe;
	}

	@Override
	public Map<String, Payment> update(Map<String, Payment> repository) throws InterruptedException {
		Payment currentPayment = repository.get(payment.getIndex());
		if (currentPayment == null) {
			pipe.put(UpdateResult.DOES_NOT_EXIST);
		} else if (!currentPayment.getDigest().equals(digest)) {
			pipe.put(UpdateResult.MISMATCHED_DIGESTS);
		} else if (!currentPayment.getIndex().equals(index)) {
			pipe.put(UpdateResult.MISMATCHED_IDS);
		} else {
			repository.put(payment.getIndex(), payment);
			pipe.put(UpdateResult.SUCCESS);
		}
		return repository;
	}
}
