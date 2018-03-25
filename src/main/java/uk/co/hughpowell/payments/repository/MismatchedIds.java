package uk.co.hughpowell.payments.repository;

public class MismatchedIds extends RuntimeException {
	public MismatchedIds(String indexId, String idOfPayment) {
		super("The index ID '" + indexId + "' and the payment ID '"
	+ idOfPayment + "' do not match");
	}
}
