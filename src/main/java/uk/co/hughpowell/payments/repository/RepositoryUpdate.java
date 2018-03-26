package uk.co.hughpowell.payments.repository;

import java.util.Map;

import com.lambdista.util.Try;

interface RepositoryUpdate {
	Try<RepositoryUpdate> update(Map<String, Payment> repository) throws InterruptedException;
	void complete(Try<RepositoryUpdate> result) throws InterruptedException;
}