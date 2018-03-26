package uk.co.hughpowell.payments.validation;

public class MismatchedIdsException extends RuntimeException {
	public MismatchedIdsException(String indexId, String idOfPayment) {
		super("The index ID '" + indexId + "' and the payment ID '"
	+ idOfPayment + "' do not match");
	}
}
