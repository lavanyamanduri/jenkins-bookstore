package com.bridgelabz.bookstore.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name="admin")
@Data
public class Admin {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long adminId;
	
	
	private String name;
	
	
	private String email;
	
	private String  Profile;
	
	private String password;
	
	
	private Long mobileNumber;
	
	@Column(columnDefinition = "boolean Default false", nullable = false)	
	private boolean isverified;
	
	

	

}

