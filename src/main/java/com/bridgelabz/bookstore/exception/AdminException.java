package com.bridgelabz.bookstore.exception;


import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@ToString
public class AdminException extends Exception {

	private static final long serialVersionUID = 1L;
	HttpStatus code;

	public AdminException(HttpStatus code, String message) {

		super(message);
		this.code = code;
	}

	public HttpStatus getCode() {
		return code;
	}

	public void setCode(HttpStatus code) {
		this.code = code;
	}


}



