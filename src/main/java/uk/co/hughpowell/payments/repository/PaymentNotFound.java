package uk.co.hughpowell.payments.repository;

public class PaymentNotFound extends RuntimeException {
	public PaymentNotFound(String paymentId) {
		super("could not find payment with Id: '" + paymentId + "'");
	}
}
