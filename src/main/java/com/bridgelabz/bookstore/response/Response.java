package com.bridgelabz.bookstore.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public class Response {

	private HttpStatus code;
	private String message;
	private Object object;

	public Response(HttpStatus code, String message, Object object) {
		super();
		this.code = code;
		this.message = message;
		this.object = object;
	}

	public Response(HttpStatus code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Response(int i, String message2, Object object2) {
		
	}

	public HttpStatus getCode() {
		return code;
	}

	public void setCode(HttpStatus code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
