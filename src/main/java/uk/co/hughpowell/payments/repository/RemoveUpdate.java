package uk.co.hughpowell.payments.repository;

import java.util.Map;

class RemoveUpdate implements RepositoryUpdate {

	private final String paymentId;
	
	RemoveUpdate(String paymentId) {
		this.paymentId = paymentId;
	}
	
	@Override
	public Map<String, Payment> update(Map<String, Payment> repository) {
		repository.remove(paymentId);
		return repository;
	}
}
