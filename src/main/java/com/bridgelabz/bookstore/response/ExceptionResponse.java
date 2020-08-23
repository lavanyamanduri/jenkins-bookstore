package com.bridgelabz.bookstore.response;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {

	private HttpStatus code;
	private String message;

	public ExceptionResponse(HttpStatus code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public HttpStatus getCode() {
		return code;
	}

	public void setMessagecode(HttpStatus code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
