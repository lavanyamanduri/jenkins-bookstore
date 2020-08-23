package com.bridgelabz.bookstore.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name = "book")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookId;

	private String bookName;

	private String bookAuthor;
	
	private Long noOfBooks;
	private double bookPrice;
	private String bookimage;
	private String approveStatus;
	private String bookDescription;
	@Column(columnDefinition = "boolean Default true", nullable = false)
	private boolean isBookApproved;
	private double avgRate;

	private LocalDateTime bookCreatedAt;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Reviews> reviewRating;

	


}
