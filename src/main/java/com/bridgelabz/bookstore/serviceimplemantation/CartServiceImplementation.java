package com.bridgelabz.bookstore.serviceimplemantation;

import java.util.List;
import javax.transaction.Transactional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.CartDetails;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.repository.AdminRepository;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.repository.CartRepository;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.service.CartService;
import com.bridgelabz.bookstore.utility.JwtService;

@Service
public class CartServiceImplementation implements CartService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	BookRepository bookRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	AdminRepository adminRepository;

	/**
	 * 
	 * This api is for add the book into the cart
	 * 
	 */

	@Transactional
	@Override
	public List<CartDetails> addBooksInTOTheCart(String token, Long bookId) throws UserException, BookException {
		Long id = JwtService.parse(token);
		long quantity = 1;
		int flag = 0;
		Long bkid = bookId;
		CartDetails cart = new CartDetails();
		Users userInfo = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user not found"));
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book not found"));

		if (userInfo.getBooksCart().isEmpty()) {
			cart.setBooks(book);
			cart.setQuantityOfBooks(quantity);
			cart.setTotalCost(book.getBookPrice());
			userInfo.getBooksCart().add(cart);
			 userRepository.save(userInfo);
			Book bookkk = bookRepository.findById(bookId).orElseThrow(
					() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
			Long quant = bookkk.getNoOfBooks() - 1;
			bookkk.setNoOfBooks(quant);
			bookRepository.save(bookkk);
		} else {
			for (CartDetails carts : userInfo.getBooksCart()) {
				Book bookss = carts.getBooks();
				if (bookss.getBookId().equals(bookId)) {

					throw new BookException(HttpStatus.NOT_FOUND, "Book is already prasent in cart");
				} else {

					flag = 1;

				}
			}
		}
		if (flag == 1) {
			cart.setBooks(book);
			cart.setQuantityOfBooks(quantity);
			cart.setTotalCost(book.getBookPrice());
			userInfo.getBooksCart().add(cart);
			userRepository.save(userInfo);
			Book bookkk = bookRepository.findById(bkid).orElseThrow(
					() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
			Long quant = bookkk.getNoOfBooks() - 1;
			bookkk.setNoOfBooks(quant);
			bookRepository.save(bookkk);
		}
		
		for (Book wishlistBooks : userInfo.getWhilistBooks()) {
			if (wishlistBooks.getBookId().equals(bookId)) {
				userInfo.getWhilistBooks().remove(book);
				userRepository.save(userInfo);
				break;
			} 
		}
		
		return userInfo.getBooksCart();
	}

	/**
	 * 
	 * This api is for get all the books from the cart
	 * 
	 */
	@Transactional
	@Override
	public List<CartDetails> getBooksfromCart(String token) throws UserException {
		Long id = JwtService.parse(token);
		Users userInfo = userRepository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));

		return userInfo.getBooksCart();
	}

	/**
	 * 
	 * This api is for remove from the cart
	 * 
	 */

	@Transactional
	@Override
	public List<CartDetails> removeBooksFromCart(Long cartId, String token) throws UserException, BookException {

		Long id = JwtService.parse(token);
		Book bookss;
		Users userInfo = userRepository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		CartDetails cartdetails = userInfo.getBooksCart().stream().filter((cart) -> cart.getCartId() == cartId)
				.findFirst().orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND,
						ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		long quant = cartdetails.getQuantityOfBooks();
		bookss = cartdetails.getBooks();

		userInfo.getBooksCart().remove(cartdetails);

		userRepository.save(userInfo);
		// cartRepository.deleteCartItem(cartId);

		Book bookkk = bookRepository.findById(bookss.getBookId()).orElseThrow(
				() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		Long qnty = bookkk.getNoOfBooks();
		System.out.print("ssssss" + quant);
		Long qty = qnty + quant;
		bookkk.setNoOfBooks(qty);
		bookRepository.save(bookkk);
		return userInfo.getBooksCart();

	}

	/**
	 * 
	 * This api is for increment the book quantity
	 * 
	 */

	@Transactional
	@Override
	public List<CartDetails> addBooksQuantityToCart(String token, Long cartId) throws UserException, BookException {
		Long id = JwtService.parse(token);
		Book bookss = null;
		Users userInfo = userRepository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		CartDetails cartdetails = userInfo.getBooksCart().stream().filter((cart) -> cart.getCartId() == cartId)
				.findFirst().orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND,
						ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		long quant = cartdetails.getQuantityOfBooks();
		long quantities = quant + 1;
		cartdetails.setQuantityOfBooks(quantities);
		userRepository.save(userInfo);
		for (CartDetails carts : userInfo.getBooksCart()) {
			bookss = carts.getBooks();

		}
		Book bookkk = bookRepository.findById(bookss.getBookId()).orElseThrow(
				() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		Long qnty = bookkk.getNoOfBooks();
		Long qty = qnty - 1;
		bookkk.setNoOfBooks(qty);
		bookRepository.save(bookkk);
		return userInfo.getBooksCart();
	}

	/**
	 * 
	 * This api is for decrement the book quantity
	 * 
	 */
	@Transactional
	@Override
	public List<CartDetails> decreasingBooksQuantityInCart(String token, Long cartId)
			throws UserException, BookException {
		Long id = JwtService.parse(token);
		Book bookss = null;
		Users userInfo = userRepository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		CartDetails cartdetails = userInfo.getBooksCart().stream().filter((cart) -> cart.getCartId() == cartId)
				.findFirst().orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND,
						ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		long quant = cartdetails.getQuantityOfBooks();
		long quantities = quant - 1;
		cartdetails.setQuantityOfBooks(quantities);
		userRepository.save(userInfo);
		for (CartDetails carts : userInfo.getBooksCart()) {
			bookss = carts.getBooks();

		}
		Book bookkk = bookRepository.findById(bookss.getBookId()).orElseThrow(
				() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		Long qnty = bookkk.getNoOfBooks();
		Long qty = qnty + 1;
		bookkk.setNoOfBooks(qty);
		bookRepository.save(bookkk);
		return userInfo.getBooksCart();
	}

	/**
	 * 
	 * This api is for increment decrement the books quantity
	 * 
	 */
	@Override
	public List<CartDetails> multipleIncrementAndDecrementQuantity(String token, Long cartId, Long quantity)
			throws UserException, BookException {
		Long id = JwtService.parse(token);
		Book bookss = null;
		long quant;
		long newquant;	
		double newtotal;
		double totalAmount;
		double totalcost;
		Users userInfo = userRepository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		CartDetails cartdetails = userInfo.getBooksCart().stream().filter((cart) -> cart.getCartId() == cartId)
				.findFirst().orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND,
						ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		quant = cartdetails.getQuantityOfBooks();
		totalAmount = cartdetails.getTotalCost();
		totalcost = (totalAmount / quant);
		newtotal = (totalcost * quantity);
		cartdetails.setQuantityOfBooks(quantity);
		cartdetails.setTotalCost(newtotal);	
		for (CartDetails carts : userInfo.getBooksCart()) {
			bookss = carts.getBooks();
		}
		userRepository.save(userInfo);
		if (quantity < quant) {
			newquant = (quant - quantity);
			Book bookkk = bookRepository.findById(bookss.getBookId()).orElseThrow(
					() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
			Long qnty = bookkk.getNoOfBooks();
			Long qty = qnty + newquant;
			bookkk.setNoOfBooks(qty);
			bookRepository.save(bookkk);
		} else if(quantity > quant) {
			newquant = (quantity - quant);
			Book bookkk = bookRepository.findById(bookss.getBookId()).orElseThrow(
					() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
			Long qnty = bookkk.getNoOfBooks();
			Long qty = qnty - newquant;
			if (qty == 0) {
				throw new UserException(HttpStatus.NOT_FOUND, "Book is not available");
			} else {
				bookkk.setNoOfBooks(qty);
				bookRepository.save(bookkk);
			}
		}
		else {
			throw new UserException(HttpStatus.NOT_FOUND, "Please select quantity");
		}
		return userInfo.getBooksCart();
	}

}
