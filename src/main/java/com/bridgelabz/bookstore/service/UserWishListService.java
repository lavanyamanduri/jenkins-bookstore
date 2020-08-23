package com.bridgelabz.bookstore.service;

import java.util.List;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.entity.Book;

public interface UserWishListService {

	public Users addBooksTiWishList(String token, long bookId) throws UserException, BookException;

	public Users removeBooksTiWishList(String token, long bookId) throws UserException, BookException;

	public List<Book> viewAllBooksFromWishList(String token) throws UserException, BookException;
}
