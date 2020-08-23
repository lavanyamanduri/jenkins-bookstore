package com.bridgelabz.bookstore.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AdminPasswordDto {

	
	
	@NotBlank
	private String Oldpassword;
	
	@NotBlank
	private String newPassword;
	
	@NotBlank
	private String confirmPassword;
	
	

	
}
