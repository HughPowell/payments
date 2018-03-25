package uk.co.hughpowell.payments.repository;

public class NullDigest extends RuntimeException {
	public NullDigest() {
		super("Digest must not be null");
	}
}
