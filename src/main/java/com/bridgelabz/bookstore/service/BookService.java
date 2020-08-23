package com.bridgelabz.bookstore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.dto.BookDto;
import com.bridgelabz.bookstore.dto.ReviewDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Reviews;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;

public interface BookService {

	List<Book> displayBooks(Integer page) throws BookException;

	Book displaySingleBook(Long id) throws BookException;

	public Book updateBook(String token, Long bookId, BookDto dto) throws Exception;

	public Book addBook(String token, BookDto dto) throws SellerException, S3BucketException, IOException;

	public List<Reviews>  writeReviewAndRating(String token, ReviewDto rrDTO, Long bookId) throws UserException, BookException;

	public List<Reviews> getRatingsOfBook(Long bookId);

	List<Book> sortByPriceAsc(Integer page) throws BookException;

	List<Book> sortByPriceDesc(Integer page) throws BookException;

	List<Book> sortByNewest(Integer page) throws BookException;
	public Book addProfile(Long id ,MultipartFile file)throws BookException, S3BucketException, IOException;
	Integer getCountOfBooks();
	public List<Book> getEachApprovedBook(String token) throws UserException;
	
	public Book removeProfile(String token,Long bookId)throws BookException, S3BucketException;
}
