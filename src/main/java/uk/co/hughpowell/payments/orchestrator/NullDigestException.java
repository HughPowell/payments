package uk.co.hughpowell.payments.orchestrator;

public class NullDigestException extends RuntimeException {
	public NullDigestException() {
		super("Digest must not be null");
	}
}
