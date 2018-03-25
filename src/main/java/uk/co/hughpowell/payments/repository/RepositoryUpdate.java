package uk.co.hughpowell.payments.repository;

import java.util.Map;

interface RepositoryUpdate {
	Map<String, Payment> update(Map<String, Payment> repository) throws InterruptedException;
}