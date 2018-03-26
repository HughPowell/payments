package uk.co.hughpowell.payments.controller;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.co.hughpowell.payments.validation.MismatchedDigestsException;
import uk.co.hughpowell.payments.validation.MismatchedIdsException;
import uk.co.hughpowell.payments.validation.NullDigestException;
import uk.co.hughpowell.payments.validation.PaymentAlreadyExistsException;
import uk.co.hughpowell.payments.validation.PaymentNotFoundException;

@ControllerAdvice
public class PaymentsControllerAdvice {
	@ResponseBody
	@ExceptionHandler(PaymentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	VndErrors paymentNotFoundHandler(PaymentNotFoundException ex) {
		return new VndErrors("error", ex.getMessage());
	}
		
	@ResponseBody
	@ExceptionHandler(PaymentAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	VndErrors paymentAlreadyExistsHandler(PaymentAlreadyExistsException ex) {
		return new VndErrors("error", ex.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(MismatchedIdsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	VndErrors mismatchedIdsHandler(MismatchedIdsException ex) {
		return new VndErrors("error", ex.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(MismatchedDigestsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	VndErrors mismatchedDigestsHandler(MismatchedDigestsException ex) {
		return new VndErrors("error", ex.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(NullDigestException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
	VndErrors nullDigestHandler(NullDigestException ex) {
		return new VndErrors("error", ex.getMessage());
	}
}
