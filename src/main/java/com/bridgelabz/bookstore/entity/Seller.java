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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.UniqueElements;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
public class Seller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long sellerId;
	@Column
	@NotNull
	private String name;
	@Column
	@NotNull
	private String email;
	@Column
	@NotNull
	private String password;
	@Column
	@NotNull
	private Long mobileNumber;
	private String profile;
	@NotNull
	@Column(columnDefinition = "boolean Default false", nullable = false)
	private boolean isVerified;
	@Column
	private LocalDateTime dateTime;
	

	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "sellerId")
	private List<Book> sellerBooks;
	

}
