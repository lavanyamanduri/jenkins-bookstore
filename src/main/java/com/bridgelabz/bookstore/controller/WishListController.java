package com.bridgelabz.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.service.UserWishListService;

import io.swagger.annotations.ApiOperation;
@RestController
@CrossOrigin
@RequestMapping("/wishlist")
public class WishListController {


	@Autowired
	private UserWishListService userWishListService;
	
	@ApiOperation(value = "Adding the books to the Whishlist",response = Iterable.class)
	@PostMapping(value="/add_book")
	public ResponseEntity<Response> addBooksToWhilist(@RequestHeader("token") String token,@RequestParam("bookId") long bookId) throws Exception {
		    Users whishlist = userWishListService.addBooksTiWishList(token, bookId);
		    		
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, "bookDetails are verified", whishlist));
  	
	}
	@ApiOperation(value = "Getting the books from Whishlist",response = Iterable.class)
	@GetMapping(value="/getbook")
	public ResponseEntity<Response> getBooksfromCart(@RequestHeader("token") String token) throws Exception {
		    List<Book> whishlist = userWishListService.viewAllBooksFromWishList(token);
		    		
		    return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, "bookDetails are verified", whishlist));
	}
	
	@ApiOperation(value = "Removing the books to the Whishlist",response = Iterable.class)
	@PostMapping(value="/removebook")
	public ResponseEntity<Response> removeBooksToWhilist(@RequestHeader("token") String token,@RequestParam("bookId") long bookId) throws Exception {
		    Users whishlist = userWishListService.removeBooksTiWishList(token, bookId);
		    		
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, "bookDetails are verified", whishlist));
 	
	}
}
