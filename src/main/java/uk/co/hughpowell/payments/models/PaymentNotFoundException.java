package uk.co.hughpowell.payments.models;

public class PaymentNotFoundException extends RuntimeException {
	public PaymentNotFoundException(String paymentId) {
		super("could not find payment with Id: '" + paymentId + "'");
	}
}
