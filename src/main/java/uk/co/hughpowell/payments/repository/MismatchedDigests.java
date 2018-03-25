package uk.co.hughpowell.payments.repository;

public class MismatchedDigests extends RuntimeException {
	public MismatchedDigests() {
		super("Digests are mismatched");
	}
}
