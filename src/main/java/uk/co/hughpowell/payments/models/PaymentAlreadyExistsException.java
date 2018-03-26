package uk.co.hughpowell.payments.models;

public class PaymentAlreadyExistsException extends RuntimeException {
	public PaymentAlreadyExistsException(String paymentId) {
		super("A payment with ID '" + paymentId + "' already exists");
	}
}
