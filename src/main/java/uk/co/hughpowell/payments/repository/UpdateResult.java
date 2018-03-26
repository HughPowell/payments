package uk.co.hughpowell.payments.repository;

enum UpdateResult {
	SUCCESS,
	ALREADY_EXISTS,
	DOES_NOT_EXIST,
	MISMATCHED_DIGESTS,
	MISMATCHED_IDS
}
