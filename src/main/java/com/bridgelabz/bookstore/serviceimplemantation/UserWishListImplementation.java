package com.bridgelabz.bookstore.serviceimplemantation;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.service.UserWishListService;
import com.bridgelabz.bookstore.utility.JwtService;

@Service
public class UserWishListImplementation implements UserWishListService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BookRepository bookRepository;

	@Transactional
	@Override
	public Users addBooksTiWishList(String token, long bookId) throws UserException, BookException {
		Long id = null;
		id = JwtService.parse(token);
		Users userInfo = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user does not exist"));
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book does not exist"));
		if (userInfo.getWhilistBooks() == null) {
			userInfo.getWhilistBooks().add(book);
		}
		Optional<Book> wishlist = userInfo.getWhilistBooks().stream().filter(wish -> wish.getBookId() == bookId)
				.findFirst();
		if (wishlist.isPresent()) {
			throw new BookException(HttpStatus.ALREADY_REPORTED, "Book is already in wish list");
		} else {
			userInfo.getWhilistBooks().add(book);
		}
		return userRepository.save(userInfo);
	}

	@Override
	public Users removeBooksTiWishList(String token, long bookId) throws UserException, BookException {
		Long id = null;
		id = JwtService.parse(token);
		Users userInfo = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user does not exist"));
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book does not exist"));
		userInfo.getWhilistBooks().remove(book);
		return userRepository.save(userInfo);
	}

	@Override
	public List<Book> viewAllBooksFromWishList(String token) throws UserException, BookException {
		Long id = null;
		id = JwtService.parse(token);
		Users userInfo = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user does not exist"));
		List<Book> whishList = userInfo.getWhilistBooks();
		return whishList;
	}

}
