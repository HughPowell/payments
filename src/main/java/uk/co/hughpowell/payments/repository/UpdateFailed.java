package uk.co.hughpowell.payments.repository;

public class UpdateFailed extends RuntimeException {
	public UpdateFailed(RepositoryUpdate update) {
		super("Repository update failed: " + update);
	}
}
