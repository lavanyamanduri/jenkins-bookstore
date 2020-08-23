package com.bridgelabz.bookstore.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class BookDto {

	@NotBlank
	private String bookName;

	@NotBlank
	private String bookAuthor;

	private double bookPrice;


	@NotBlank
	private String bookDescription;

	
	private Long noOfBooks;

	



}
