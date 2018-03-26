package uk.co.hughpowell.payments.validation;

public class NullDigestException extends RuntimeException {
	public NullDigestException() {
		super("Digest must not be null");
	}
}
