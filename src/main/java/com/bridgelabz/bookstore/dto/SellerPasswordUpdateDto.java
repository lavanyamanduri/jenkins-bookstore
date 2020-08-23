package com.bridgelabz.bookstore.dto;

import com.sun.istack.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SellerPasswordUpdateDto {
	@NotNull
	String NewPassword;
	@NotNull
	String  ConfirmPassword;
	
	
}
