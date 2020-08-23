package com.bridgelabz.bookstore.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.UserException;


public interface AdminService {

	Admin adminRegistartion(RegisterDto adminInformation)throws AdminException  ;

	boolean verifyAdmin(String token) throws AdminException ;

	Admin loginToAdmin(LoginDto adminLogin) throws AdminException;
	
	Admin forgetPassword(String email) throws AdminException;

	boolean updatepassword(AdminPasswordDto information, String token) throws AdminException;

	boolean approveBook(Long id,String approveStatus) throws BookException;
 	
	public boolean resetPassword(ResetPassword update,String token) throws AdminException;
	
 	public List<Book> getNotapproveBook(String token) throws AdminException;
 	public Admin addProfile(MultipartFile file,String  token)throws AdminException,S3BucketException,AmazonServiceException, SdkClientException, IOException;
 	public Admin getAdminById(String token) throws AdminException;
 	public Admin removeProfile(String token)throws AdminException, S3BucketException;
}
