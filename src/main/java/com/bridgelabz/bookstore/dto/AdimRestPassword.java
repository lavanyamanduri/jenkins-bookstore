package com.bridgelabz.bookstore.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AdimRestPassword {
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
}