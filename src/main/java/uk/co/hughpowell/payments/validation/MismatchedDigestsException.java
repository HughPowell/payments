package uk.co.hughpowell.payments.validation;

public class MismatchedDigestsException extends RuntimeException {
	public MismatchedDigestsException() {
		super("Digests are mismatched");
	}
}
