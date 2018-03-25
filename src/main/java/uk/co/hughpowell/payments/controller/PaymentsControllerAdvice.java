package uk.co.hughpowell.payments.controller;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.co.hughpowell.payments.repository.MismatchedIds;
import uk.co.hughpowell.payments.repository.PaymentNotFound;

@ControllerAdvice
public class PaymentsControllerAdvice {
	@ResponseBody
	@ExceptionHandler(PaymentNotFound.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	VndErrors paymentNotFoundHandler(PaymentNotFound ex) {
		return new VndErrors("error", ex.getMessage());
	}
		
	@ResponseBody
	@ExceptionHandler(MismatchedIds.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	VndErrors mismatchedIdsHandler(MismatchedIds ex) {
		return new VndErrors("error", ex.getMessage());
	}
}
