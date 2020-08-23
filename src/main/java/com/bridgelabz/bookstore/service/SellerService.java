package com.bridgelabz.bookstore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;

public interface SellerService {

	public Seller register(RegisterDto dto);

	public Seller login(LoginDto login);

	public Boolean verify(String token);

	public Boolean resetPassword(ResetPassword update, String token);

	public Seller forgetPassword(String email);

	public Seller addProfile(MultipartFile file, String token)
			throws SellerException, AmazonServiceException, SdkClientException, S3BucketException, IOException;
	public Seller getSellerById(String token)throws SellerException;
	public Seller removeProfile(String token)throws SellerException, S3BucketException;
	
	
	public List<Book> getBooks(String token) throws SellerException;

}
