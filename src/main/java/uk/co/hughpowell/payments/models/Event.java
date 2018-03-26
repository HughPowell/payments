package uk.co.hughpowell.payments.models;

import java.util.Map;

import com.lambdista.util.Try;

public interface Event {
	Try<Event> update(Map<String, Payment> repository) throws InterruptedException;
	void complete(Try<Event> result) throws InterruptedException;
}