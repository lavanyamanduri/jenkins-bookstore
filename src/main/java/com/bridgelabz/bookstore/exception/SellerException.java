package com.bridgelabz.bookstore.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
 
/*..exception for seller...*/

@Data
@NoArgsConstructor
@ToString
public class SellerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	HttpStatus code;

	public SellerException(HttpStatus code, String message) {

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
