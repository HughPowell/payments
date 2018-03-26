package uk.co.hughpowell.payments.validation;

public class PaymentAlreadyExistsException extends RuntimeException {
	public PaymentAlreadyExistsException(String paymentId) {
		super("A payment with ID '" + paymentId + "' already exists");
	}
}
