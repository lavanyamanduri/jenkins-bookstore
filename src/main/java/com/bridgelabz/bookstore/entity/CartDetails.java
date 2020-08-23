package com.bridgelabz.bookstore.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="cart")
public class CartDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private  Long cartId;
	
	private long quantityOfBooks;
	
	private double totalCost;
	
	@OneToOne(cascade = CascadeType.ALL,targetEntity = Book.class)
	private Book books;
	 
	
}
