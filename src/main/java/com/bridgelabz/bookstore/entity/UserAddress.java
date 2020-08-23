package com.bridgelabz.bookstore.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Table(name = "userAddress")
@Entity
public class UserAddress {
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long addressId;
	 @NotNull
	 private String landmark;
	 @NotNull
	 private String city;
	 @NotNull
	 private String country;
	 @NotNull
	 private String address;
	 @NotNull
	 private String addressType;
	 @NotNull
	 private int pinCode;
	 @NotNull
	 private String name;
	 @NotNull
	 private long phonenumber;

	 
	
}