package com.bridgelabz.bookstore.controller;


import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

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
import com.bridgelabz.bookstore.dto.AdimRestPassword;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.AdminPasswordDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.serviceimplemantation.AdminServiceImplementation;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.JwtService.Token;

import io.swagger.annotations.ApiOperation;



@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

	
	@Autowired
	private AdminServiceImplementation service;
	
	
	@ApiOperation(value = "Api Registerartion Admin",response = Iterable.class)
	@PostMapping("/registration")
	public ResponseEntity<Response> registration(@Valid @RequestBody RegisterDto admiInformation) throws AdminException  {
		Admin result = service.adminRegistartion(admiInformation);

		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.REGISTER_SUCCESSFULL, result));
	}
	
	@ApiOperation(value = "Api for Admin email verification",response = Iterable.class)
	@PutMapping("/resetpassword/{token}")
	public ResponseEntity<Response> resetAdmin(@RequestBody ResetPassword reset ,@PathVariable("token")String token) throws AdminException {
		
		boolean update = service.resetPassword(reset,token);	
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, "password update successfully", update));	
		
	}
	
	@ApiOperation(value = "Api for Admin email verification",response = Iterable.class)
	@PutMapping("/verifyemail/token")
	public ResponseEntity<Response> verficationAdmin( @PathVariable("token") String token) throws AdminException {
		
		boolean update = service.verifyAdmin(token);	
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.AMDIN_VERIFIED_SUCCESSFULL, update));	
		
	}
	
	
	@ApiOperation(value = "Api for admin login",response = Iterable.class)
	@PostMapping("/login")
	public ResponseEntity<Response> loginAdmin(@Valid @RequestBody LoginDto adminLogin,BindingResult res) throws AdminException {
		   if(res.hasErrors()) {
	    	   return ResponseEntity.badRequest().body(new Response(HttpStatus.NOT_ACCEPTABLE,ExceptionMessages.USER_REGISTER_STATUS_INFO,adminLogin));
	     }
		Admin admin = service.loginToAdmin(adminLogin);
		String mailResponse = JwtService.generateToken(admin.getAdminId(), Token.WITH_EXPIRE_TIME);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "login successfull", mailResponse));		
	}
	
	
	@ApiOperation(value = "Api forgotpassword for admin",response = Iterable.class)
	@PostMapping("/forgotpassword")
	public ResponseEntity<Response> forgotPassword(@RequestParam("email") String email) throws AdminException {
		Admin result = service.forgetPassword(email);		
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED,"reset password mail hasbeen send to admin succeessfully", email));		
	}

	
	@ApiOperation(value = "Api for change admin password",response = Iterable.class)
	@PutMapping("/update/{token}")
	public ResponseEntity<Response> updatePassword(@PathVariable("token") String token,
			@RequestBody AdminPasswordDto update) throws AdminException {
		boolean result = service.updatepassword(update, token);	
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.PASSWORD_UPDATE_SUCCESFULL, result));
		
	}
	
	
	@ApiOperation(value = "Api for approve books from admin",response = Iterable.class)
	@PostMapping("/approvebook")
	public ResponseEntity<Response> approveTheBook(@RequestParam("bookid") Long bookId,@RequestParam("approveStatus")String approveStatus) throws BookException {
		boolean result = service.approveBook(bookId,approveStatus);		
			if(result) {
		return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.BOOK_APPROVED, result));
			}else {
				return ResponseEntity.ok()
						.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.BOOK_HOLD_REJECT, result));
			
			}

	}
	
	@ApiOperation(value = "Api for get not approve books from admin",response = Iterable.class)
	@GetMapping("/get_not_approvebooks")
	public ResponseEntity<Response> approveTheBook(@RequestHeader("token") String token) throws BookException, AdminException {
		List<Book> result = service.getNotapproveBook(token);					
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.BOOK_APPROVED, result));
		

	}
	@ApiOperation(value="add profile to admin",response = Iterable.class )
	@PutMapping("/profile")
	public ResponseEntity<Response> addProfile( @RequestPart("file") MultipartFile file ,@RequestHeader("token") String token) throws S3BucketException, AmazonServiceException, SdkClientException, AdminException, IOException{
		Admin admin =service.addProfile(file, token);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "profile added for admin", admin));
	
	}
	@ApiOperation(value="get  the admin details by admin id" ,response = Iterable.class) 
	@GetMapping("/getadmin")
	public ResponseEntity<Response> getAdmin(@RequestHeader("token") String token)
			throws AdminException {
		Admin admin = service.getAdminById(token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, " admin details are...", admin));

	} 

	@ApiOperation(value="remove profile to admin",response = Iterable.class )
	@DeleteMapping("/removeprofile")
	public ResponseEntity<Response> removeProfile(@RequestHeader("token") String token) throws S3BucketException, AdminException{
		Admin admin =service.removeProfile(token);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "profile pic removed", admin));
	
	}
	
}

