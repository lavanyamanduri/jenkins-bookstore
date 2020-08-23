package com.bridgelabz.bookstore.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.dto.BookDto;
import com.bridgelabz.bookstore.dto.ReviewDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Reviews;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.service.BookService;
import com.bridgelabz.bookstore.service.ElasticSearchService;
import com.bridgelabz.bookstore.service.UserWishListService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookController {
	@Autowired
	private ElasticSearchService elasticService;

	@Autowired
	private BookService bookService;
	ObjectMapper objectmapper = new ObjectMapper();

	@ApiOperation(value = "Api diplay all books", response = Iterable.class)
	@GetMapping("/displaybooks/{page}")
	public ResponseEntity<Response> displayAllBooks(@PathVariable("page") Integer page) throws BookException {
		List<Book> books = bookService.displayBooks(page);
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "books for user displayed", books));

	}

	@ApiOperation(value = "Api count all books", response = Iterable.class)
	@GetMapping("/count")
	public ResponseEntity<Response> countOfBooks() throws BookException {
		Integer count = bookService.getCountOfBooks();
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "books for user displayed", count));

	}

	@ApiOperation(value = "Api diplay single book", response = Iterable.class)
	@GetMapping("/display_single_book")
	public ResponseEntity<Response> displayParticularBook(@RequestParam("id") Long id) throws BookException {
		Book book = bookService.displaySingleBook(id);
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "Particular book not displayed", book));
	}

	@ApiOperation(value = "Api search the book", response = Iterable.class)
	@GetMapping("/search")
	public ResponseEntity<Response> searchBooks(@RequestParam("title") String title) {
		List<Book> books = elasticService.getBookByTitleAndAuthor(title);
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "Books searched and Found", books));

	}

	@ApiOperation(value = "Api for sort books in asc order by price", response = Iterable.class)
	@GetMapping("/sortbypriceasc/{page}")
	public ResponseEntity<Response> sortByPriceLowtoHigh(@PathVariable("page") Integer page) throws BookException {
		List<Book> books = bookService.sortByPriceAsc(page);
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "Book by Price low to high Found", books));

	}

	@ApiOperation(value = "Api for sort books in desc order by price", response = Iterable.class)
	@GetMapping("/sortbypricedesc/{page}")
	public ResponseEntity<Response> sortByPriceHightoLow(@PathVariable("page") Integer page) throws BookException {
		List<Book> books = bookService.sortByPriceDesc(page);
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "Book by Price high to low Found", books));

	}

	@ApiOperation(value = "Api for sort book by new arrivals", response = Iterable.class)
	@GetMapping("/sortbynewest/{page}")
	public ResponseEntity<Response> sortByNewestArrivals(@PathVariable("page") Integer page) throws BookException {
		List<Book> books = bookService.sortByNewest(page);
		return ResponseEntity.ok().body(new Response(HttpStatus.FOUND, "Book by Newest Arrivals Found", books));

	}

	/* API for seller adding books for approval */
	@PostMapping( "/addbook" )
	@ApiOperation("seller adding books")
	public ResponseEntity<Response> addBook(@RequestBody(required = true) BookDto dto,
			@RequestHeader("token") String token)
			throws SellerException, AmazonServiceException, SdkClientException, IOException, S3BucketException {
		Book addBook = bookService.addBook(token, dto);

		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "book added  successfully", addBook));

	}

	@PutMapping(value = "/updatebook")
	@ApiOperation("seller updating books")
	public ResponseEntity<Response> updatebook(@RequestBody(required = true) BookDto dto,
			@RequestParam Long bookId, @RequestHeader("token") String token) throws Exception {
		Book addBook = bookService.updateBook(token, bookId, dto);

		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "book updated  successfully", addBook));

	}

	@ApiOperation(value = "Api for rating and review the book")
	@PostMapping("/ratingreview")
	public ResponseEntity<Response> writeReview(@RequestBody ReviewDto review,
			@RequestHeader(name = "token") String token, @RequestParam Long bookId)
			throws UserException, BookException {
		List<Reviews> reviews=bookService.writeReviewAndRating(token, review, bookId);

		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "review added for book successfully", reviews));

	}

	@ApiOperation(value = "Api for view all rating and review")
	@GetMapping("/viewratings")
	public ResponseEntity<Response> getBookRatingAndReview(@RequestParam Long bookId) {
		List<Reviews> review = bookService.getRatingsOfBook(bookId);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "reviews for a book are....", review));
	}

	@ApiOperation(value = "remove profile to book", response = Iterable.class)
	@DeleteMapping("/removeprofile")
	public ResponseEntity<Response> removeProfile(@RequestHeader("token") String token, Long bookId)
			throws S3BucketException, BookException {
		Book book = bookService.removeProfile(token, bookId);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "profile pic removed", book));

	}

	@ApiOperation(value = "Api for getting approved books",response = Iterable.class)
	@GetMapping("/approvedbooks")
	public ResponseEntity<Response> getApprovedBooks(@RequestParam("token") String token) throws BookException, UserException {
		List<Book> result = bookService.getEachApprovedBook(token);					
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.BOOK_APPROVED, result));
		

	}
	@ApiOperation(value = "add image to book", response = Iterable.class)
	@PutMapping("/profile")
	public ResponseEntity<Response> addProfile(@RequestPart("file") MultipartFile file,
			@RequestParam("bookId") Long bookId)
			throws S3BucketException, AmazonServiceException, SdkClientException, AdminException, IOException, BookException {
		Book book = bookService.addProfile(bookId, file);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "profile added for book", book));

	}
}
