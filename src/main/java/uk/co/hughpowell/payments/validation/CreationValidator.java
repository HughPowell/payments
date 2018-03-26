package uk.co.hughpowell.payments.validation;

import java.util.Map;

import uk.co.hughpowell.payments.models.Payment;
import uk.co.hughpowell.payments.models.Validator;

public class CreationValidator implements Validator {
	@Override
	public Payment constructionValidation(Payment payment) {
		if (payment == null) {
			throw new NullPointerException("payment must not be null");
		}
		return payment;
	}
	
	@Override
	public Payment preInsertionValidation(Map<String, Payment> map, Payment payment) {
		if (map.containsKey(payment.getIndex())) {
			throw new PaymentAlreadyExistsException(payment.getIndex());
		}
		return payment;
	}
}
