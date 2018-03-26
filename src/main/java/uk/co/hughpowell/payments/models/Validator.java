package uk.co.hughpowell.payments.models;

import java.util.Map;

public interface Validator {
	public Payment constructionValidation(Payment payment);
	public Payment preInsertionValidation(Map<String, Payment> map, Payment payment);
}
