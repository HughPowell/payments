package uk.co.hughpowell.payments.repository;

public class PaymentAlreadyExists extends RuntimeException {
	PaymentAlreadyExists(String paymentId) {
		super("A payment with ID '" + paymentId + "' already exists");
	}
}
