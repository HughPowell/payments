package uk.co.hughpowell.payments.validation;

import java.util.Map;

import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.models.Validator;

public class ReplacementValidator implements Validator {

	private final String lastKnownDigest;
	private final String indexToBeUpdated;
	
	public ReplacementValidator(String indexToBeUpdated, String lastKnownDigest) {
		this.lastKnownDigest = lastKnownDigest;
		this.indexToBeUpdated = indexToBeUpdated;
	}
	
	@Override
	public Payment constructionValidation(Payment payment) {
		if (payment == null) {
			throw new NullPointerException("payment is null");
		}
		if (lastKnownDigest == null) {
			throw new NullDigestException();
		}
		String paymentId = payment.getIndex();
		if (!indexToBeUpdated.equals(paymentId)) {
		   throw new MismatchedIdsException(indexToBeUpdated, paymentId);
	   	}
		return payment;
	}

	@Override
	public Payment preInsertionValidation(Map<String, Payment> map, Payment payment) {
		Payment currentPayment = map.get(payment.getIndex());
		if (currentPayment == null) {
			throw new PaymentNotFoundException(payment.getIndex());
		} else if (!currentPayment.getDigest().equals(lastKnownDigest)) {
			throw new MismatchedDigestsException();
		}
		return payment;
	}
}
