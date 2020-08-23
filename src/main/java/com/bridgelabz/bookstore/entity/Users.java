package com.bridgelabz.bookstore.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column
	@NotNull
	private String name;

	@Column
	@NotNull
	private String password;

	@NotNull
	@Column
	private String email;


	@Column
	@NotNull
	private Long mobileNumber;

	@Column
	@NotNull
	@CreationTimestamp
	private LocalDateTime creationTime;

	@Column(columnDefinition = "boolean default false", nullable = false)
	private boolean isverified;
	
	private String profile; 

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Order.class,fetch =FetchType.LAZY )
	@JoinColumn(name = "userId")
	private List<Order> orderBookDetails;
	
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Book> whilistBooks;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = UserAddress.class)
	@JoinColumn(name = "userId")
	private List<UserAddress> address;


	@ManyToMany(cascade = CascadeType.ALL, targetEntity = CartDetails.class,fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private List<CartDetails> booksCart;


	

}
