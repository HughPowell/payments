package uk.co.hughpowell.payments.models;

public class MismatchedDigestsException extends RuntimeException {
	public MismatchedDigestsException() {
		super("Digests are mismatched");
	}
}
