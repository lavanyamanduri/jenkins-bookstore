package com.bridgelabz.bookstore.service;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.UserAddressDto;
import com.bridgelabz.bookstore.entity.UserAddress;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.UserException;

public interface UserService {

	public Users register(@Valid RegisterDto userInfoDto) throws UserException;

	boolean verifyUser(String token) throws UserException;

	Users login(LoginDto login) throws UserException;

	Users forgetPassword(String email) throws UserException;

	boolean resetPassword(ResetPassword resetPassword, String token) throws UserException;
	
	public Users getUserById(String token) throws UserException;

	UserAddress address(UserAddressDto addressDto, String token) throws UserException;

	UserAddress updateAddress(String token, UserAddressDto addDto, String addressType) throws UserException;

	public Users addProfile(MultipartFile file, String token)
			throws AmazonServiceException, SdkClientException, IOException, UserException, S3BucketException;

	
	public UserAddress getByAddressType(String addressType, String token) throws UserException;
	public Users removeProfile(String token)throws UserException, S3BucketException;
}
