package com.bridgelabz.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDto {

		private String landmark;
	    private String city;
	    private String country;
	    private String address;
	    private String addressType;
	    private int pinCode;
		private String name;
		private long phonenumber;

}