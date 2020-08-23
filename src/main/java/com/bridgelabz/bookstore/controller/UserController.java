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

import com.bridgelabz.bookstore.dto.UserAddressDto;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.configuration.Constants;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.UserAddress;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.service.UserService;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.JwtService.Token;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

	@Autowired
	private UserService service;

	@ApiOperation(value = "Api to Register User  ",response = Response.class)
	@PostMapping("/register")
	public ResponseEntity<Response> registration( @RequestBody RegisterDto userInfoDto) throws UserException {

		Users user = service.register(userInfoDto);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.USER_REGISTER_SUCESSFULL, user));

	}

	@ApiOperation(value = "Api to verify the User ", response = Response.class)
	@PutMapping("/verifyemail/{token}")
	public ResponseEntity<Response> verification(@PathVariable("token") String token) throws UserException {

		boolean user = service.verifyUser(token);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.USER_VERIFIED_STATUS, user));
	}

	@ApiOperation(value = "Api to Login User", response = Response.class)
	@PostMapping("/login")
	public ResponseEntity<Response> login(@RequestBody LoginDto loginDto, BindingResult res) throws UserException {
		if (res.hasErrors()) {
			return ResponseEntity.badRequest().body(
					new Response(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.USER_FAILED_LOGIN_STATUS, loginDto));
		}
		Users user = service.login(loginDto);
		String mailResponse = JwtService.generateToken(user.getUserId(), Token.WITH_EXPIRE_TIME);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.USER_LOGIN_STATUS, mailResponse));
	}

	@ApiOperation(value = "Api to check if UserExists or not", response = Response.class)
	@PostMapping("/forgetpassword")
	public ResponseEntity<Response> forgetPassword(@RequestParam String email) throws UserException {
		Users user = service.forgetPassword(email);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "reset password mail send to email", email));

	}

	@ApiOperation(value = "Api to Reset User Password ", response = Response.class)
	@PutMapping("/resetpassword/{token}")
	public ResponseEntity<Response> resetPassword(@RequestBody ResetPassword password,
			@Valid @PathVariable("token") String token, BindingResult res) throws UserException {
		if (res.hasErrors()) {
			return ResponseEntity.badRequest().body(
					new Response(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.USER_RESET_PASSWORD_FAILED, password));
		}
		boolean result = service.resetPassword(password, token);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.USER_RESET_PASSWORD_SUCESSFULL, result));
	}

	@ApiOperation(value = "Api to Add User Address", response = Response.class)
	@PostMapping("/address")
	public ResponseEntity<Response> address(@RequestBody UserAddressDto addDto, @RequestHeader String token)
			throws UserException {
		UserAddress address = service.address(addDto, token);

		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.USER_ADDRESS_STATUS, address));
	}

	@ApiOperation(value = "Api to Update User Address", response = Response.class)
	@PutMapping("/updateaddress")
	public ResponseEntity<Response> updateAddress(@RequestParam String token, @RequestParam String addresstype,
			@RequestBody UserAddressDto addDto) throws UserException {
		UserAddress address = service.updateAddress(token, addDto, addresstype);
		if (address != null) {
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, ExceptionMessages.USER_UPDATE_ADDRESS_MESSAGE, address));
		}
		return ResponseEntity.ok().body(new Response(HttpStatus.NOT_FOUND, "address not found", null));

	}

	@ApiOperation(value = "add profile to user", response = Iterable.class)
	@PutMapping("/profile")
	public ResponseEntity<Response> addProfile(@RequestPart("file") MultipartFile file,
			@RequestHeader("token") String token)
			throws AmazonServiceException, S3BucketException, SdkClientException, UserException, IOException {
		Users user = service.addProfile(file, token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "profile added for user", user));

	}

	@ApiOperation(value = "get  the user details by user id", response = Iterable.class)
	@GetMapping("/getuser")
	public ResponseEntity<Response> getUser(@RequestHeader("token") String token)
			throws AmazonServiceException, S3BucketException, SdkClientException, UserException, IOException {
		Users user = service.getUserById(token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, " user details are...", user));

	}

	@ApiOperation(value = "Api to get  by AddressType ", response = Response.class)
	@GetMapping("/getaddressbyType/{addressType}")
	public ResponseEntity<Response> getByAddressType(@RequestParam String token, @PathVariable String addressType)
			throws UserException {
		UserAddress address = service.getByAddressType(addressType, token);
		if (address != null) {
			return ResponseEntity.ok()
					.body(new Response(HttpStatus.ACCEPTED, "address details fetched for user successfully", address));

		}
		return ResponseEntity.ok().body(new Response(HttpStatus.NOT_FOUND, "address not found", null));
	}

	@ApiOperation(value = "remove profile to user", response = Iterable.class)
	@DeleteMapping("/removeprofile")
	public ResponseEntity<Response> removeProfile( @RequestHeader("token") String token)
			throws S3BucketException, UserException {
		Users user = service.removeProfile(token);
		return ResponseEntity.ok().body(new Response(HttpStatus.ACCEPTED, "profile pic removed", user));

	}

}
