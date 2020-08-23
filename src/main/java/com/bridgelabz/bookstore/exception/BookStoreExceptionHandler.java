package com.bridgelabz.bookstore.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bridgelabz.bookstore.response.ExceptionResponse;

@ControllerAdvice
public class BookStoreExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BookException.class)
	public final ResponseEntity<ExceptionResponse> bookException(BookException ex) {
		ExceptionResponse exp = new ExceptionResponse(ex.getCode(), ex.getMessage());
		return ResponseEntity.status(exp.getCode()).body(exp);
	}

	@ExceptionHandler(AdminException.class)
	public final ResponseEntity<ExceptionResponse> adminNotFoundException(AdminException ex) {
		ExceptionResponse exp = new ExceptionResponse(ex.getCode(), ex.getMessage());
		return ResponseEntity.status(exp.getCode()).body(exp);
	}

	@ExceptionHandler(SellerException.class)
	public final ResponseEntity<ExceptionResponse> sellerException(SellerException ex) {
		ExceptionResponse exp = new ExceptionResponse(ex.getCode(), ex.getMessage());
		return ResponseEntity.status(exp.getCode()).body(exp);
	}

	@ExceptionHandler(UserException.class)
	public final ResponseEntity<ExceptionResponse> userException(UserException ex) {
		ExceptionResponse exp = new ExceptionResponse(ex.getCode(), ex.getMessage());
		return ResponseEntity.status(exp.getCode()).body(exp);
	}
	@ExceptionHandler(S3BucketException.class)
	public final ResponseEntity<ExceptionResponse> S3BucketException(S3BucketException ex) {
		ExceptionResponse exp = new ExceptionResponse(ex.getCode(), ex.getMessage());
		return ResponseEntity.status(exp.getCode()).body(exp);
	}
}
