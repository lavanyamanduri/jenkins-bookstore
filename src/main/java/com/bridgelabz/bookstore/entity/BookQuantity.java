package com.bridgelabz.bookstore.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Entity
@Table(name = "BookQuantity")
public class BookQuantity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long quantityId;
	
	private Long bookQty;
	
	public BookQuantity() {
		super();
	}

	public BookQuantity(Long quantityOfBook) {
		super();
		this.quantityId = quantityOfBook;
		
	}
	     
		
}
