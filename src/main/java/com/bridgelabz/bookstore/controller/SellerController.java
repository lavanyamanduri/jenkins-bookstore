package com.bridgelabz.bookstore.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.service.SellerService;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.JwtService.Token;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/seller")
@CrossOrigin
public class SellerController {

	@Autowired
	private SellerService service;

	/* API for seller registration */

	@PostMapping("/registration")
	@ApiOperation(value = "seller registration", response = Response.class)
	public ResponseEntity<Response> register(@RequestBody RegisterDto dto, BindingResult res) {
		if (res.hasErrors()) {
			return ResponseEntity.badRequest()
					.body(new Response(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.SELLER_ALREADY_MSG, dto));
		}
		Seller reg = service.register(dto);

		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "Seller Registered Successfully", reg));

	}

	/* API for seller login */
	@PostMapping("/login")
	@ApiOperation(value = "login for seller", response = Response.class)
	public ResponseEntity<Response> Login(@RequestBody LoginDto login, BindingResult res) {
		if (res.hasErrors()) {
			return ResponseEntity.badRequest()
					.body(new Response(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.SELLER_NOT_FOUND_MSG, login));
		}
		Seller seller = service.login(login);
		String token = JwtService.generateToken(seller.getSellerId(), Token.WITH_EXPIRE_TIME);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "Login Successfully", token));

	}

	/* API for verifying the token generated for the email */
	@PutMapping("/verifyemail/{token}")
	@ApiOperation(value = "seller email verification", response = Response.class)
	public ResponseEntity<Response> verify(@PathVariable("token") String token) throws Exception {

		boolean verification = service.verify(token);

		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "verified", verification));

	}

	/* API for seller forgetPassword */

	@PostMapping("/forgotpassword")
	@ApiOperation(value = "forgetpassword for seller", response = Response.class)
	public ResponseEntity<Response> forgotPassword(@RequestParam("email") String email) throws SellerException {

		Seller seller = service.forgetPassword(email);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "resetpassword mail has send to email successfully", email));

	}

	/* API for seller updating password */
	@PutMapping("/resetpassword/{token}")
	@ApiOperation(value = "reset password for seller", response = Response.class)
	public ResponseEntity<Response> resetPassword(@RequestBody ResetPassword update,
			@PathVariable("token")String token, BindingResult res) throws SellerException {
		if (res.hasErrors()) {
			return ResponseEntity.badRequest()
					.body(new Response(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.SELLER_NOT_FOUND_MSG, update));
		}
		Boolean passwordUpdate = service.resetPassword(update, token);

		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "Password updated successfully", passwordUpdate));

	}

	@ApiOperation(value = "add profile to seller", response = Iterable.class)
	@PutMapping("/profile")
	public ResponseEntity<Response> addProfile(@RequestPart("file") MultipartFile file,
			@RequestHeader("token") String token)
			throws S3BucketException, AmazonServiceException, SdkClientException, AdminException, IOException {
		Seller seller = service.addProfile(file, token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "profile added for seller", seller));

	}
	@ApiOperation(value="get  the seller details by seller id" ,response = Iterable.class) 
	@GetMapping("/getseller")
	public ResponseEntity<Response> getSeller(@RequestHeader("token") String token)
		 {
		Seller seller = service.getSellerById(token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, " seller details are...", seller));

	} 
	@ApiOperation(value="remove profile to seller",response = Iterable.class )
	@DeleteMapping("/removeprofile")
	public ResponseEntity<Response> removeProfile(@RequestHeader("token") String token) throws S3BucketException{
	Seller seller=service.removeProfile(token);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "profile pic removed", seller));
	
	}
	
	@ApiOperation(value = "list of books added by seller", response = Iterable.class)
	@GetMapping("/bookslist")
	public ResponseEntity<Response> getBooks(@RequestParam String token) throws SellerException{
		List<Book> books = service.getBooks(token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "List of books added by seller ",books));			 
		
	}
	
	
}
