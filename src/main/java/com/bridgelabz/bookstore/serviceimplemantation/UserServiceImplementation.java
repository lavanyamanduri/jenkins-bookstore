package com.bridgelabz.bookstore.serviceimplemantation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.configuration.Constants;
import com.bridgelabz.bookstore.configuration.RabbitMQSender;
import com.bridgelabz.bookstore.dto.AdminPasswordDto;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.UserAddressDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.entity.UserAddress;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.repository.UserAddressRepository;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.response.MailObject;
import com.bridgelabz.bookstore.service.UserService;
import com.bridgelabz.bookstore.utility.AwsS3Access;

import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.MailService;

import com.bridgelabz.bookstore.utility.SpringMail;

import lombok.extern.slf4j.Slf4j;

import com.bridgelabz.bookstore.utility.JwtService.Token;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

	/*
	 * User Service Class is implemented by using the object reference of Repository
	 * for database, BCryptPasswordEncoder for password
	 */

	@Autowired
	private UserRepository repository;

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Autowired
	private UserAddressRepository userAddressrepository;
	
//	@Autowired
//	private SpringMail mail;
	
	@Autowired
	private RabbitMQSender rabbitMQSender;

	
	@Autowired
	AwsS3Access s3;
	
	/*********************************************************************
	 * User to register with the required fields provided
	 * 
	 * @param UserDto userInfoDto
	 ********************************************************************/

	@Transactional
	@Override
	public Users register(@Valid RegisterDto userInfoDto) throws UserException {
		Users user = new Users();
		if (repository.FindByEmail(userInfoDto.getEmail()).isPresent()) {
			throw new UserException(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.EMAIL_ID_ALREADY_PRASENT);
		}
		BeanUtils.copyProperties(userInfoDto, user);
		user.setPassword(bcrypt.encode(userInfoDto.getPassword()));
		user.setCreationTime(LocalDateTime.now());

		user = repository.save(user);
	//	String email=user.getEmail();
		String mailResponse = Constants.USER_VERIFICATION_LINK
				+ JwtService.generateToken(user.getUserId(), Token.WITH_EXPIRE_TIME);	
		MailService.sendEmail(user.getEmail(), Constants.USER_VERIFICATION_MSG, mailResponse);
//		System.out.println(mailResponse);
		
//		MailObject mailObject = new MailObject();
//		mailObject.setEmail(user.getEmail());
//		mailObject.setMessage(mailResponse);
//		mailObject.setSubject(" user verification");
//		rabbitMQSender.send(mailObject);

		System.out.println(mailResponse);
	//	System.out.println(mailObject);
			return user;
	}

	/*********************************************************************
	 * To check whether user is verified or not by the token
	 * 
	 * @param String token
	 ********************************************************************/

	@Transactional
	@Override
	public boolean verifyUser(String token) throws UserException {

		Long id = JwtService.parse(token);

		Users userInfo = repository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		if (userInfo.isIsverified() != true) {
			userInfo.setIsverified(true);
			repository.save(userInfo);
			throw new UserException(HttpStatus.ACCEPTED, ExceptionMessages.USER_VERIFIED_STATUS);
		}

		throw new UserException(HttpStatus.BAD_REQUEST, ExceptionMessages.USER_ALREADY_VERIFIED);

	}

	/*********************************************************************
	 * To login user with the required credentials
	 * 
	 * @param UserLoginDto login
	 ********************************************************************/

	@Transactional
	@Override
	public Users login(LoginDto login) throws UserException {
		Users user = repository.FindByEmail(login.getEmail()).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));

		if ((user.isIsverified() == true) && (bcrypt.matches(login.getPassword(), user.getPassword())) != true) {

			
			throw new UserException(HttpStatus.ACCEPTED, "please enter correct email and password ");

		}
		return user;}


	@Transactional
	@Override
	public Users forgetPassword(String email) throws UserException {
		Users userMail = repository.FindByEmail(email).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));

		if (userMail.isIsverified() == true) {
			String responsemail = Constants.USER_RESETPASSWORD_LINK
					+ JwtService.generateToken(userMail.getUserId(), Token.WITH_EXPIRE_TIME);
			MailService.sendEmail(userMail.getEmail(), Constants.RESET_PASSWORD, responsemail);
			return userMail;
		}
		throw new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE);
	}

	/*********************************************************************
	 * To reset password by the user with token.
	 * 
	 * @param String token,UserPasswordDto restpassword
	 ********************************************************************/
	@Transactional
	@Override
	public boolean resetPassword(ResetPassword password, String token) throws UserException {

		Long id = JwtService.parse(token);
		Users user = repository.findById(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		if (user.isIsverified()) {
			user.setPassword(new BCryptPasswordEncoder().encode(password.getConfirmPassword()));
			repository.updateUserPassword(bcrypt.encode(password.getConfirmPassword()), id);
		}
		return true;
	}

	/*********************************************************************
	 * To add addresess by the user with token.
	 * 
	 * @param String token,UserAddressDto addressDto
	 ********************************************************************/

	@Transactional
	@Override
	public UserAddress address(UserAddressDto addressDto, String token) throws UserException {
		UserAddress users = new UserAddress();
		Long id = JwtService.parse(token);
		Users user = repository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		BeanUtils.copyProperties(addressDto, users);
		user.getAddress().add(users);
		userAddressrepository.save(users);
		return users;

	}

	/*********************************************************************
	 * To update the addresess by the user with token.
	 * 
	 * @param String token,UserAddressDto addressDto,Long addressId
	 ********************************************************************/

	@Transactional
	@Override
	public UserAddress updateAddress(String token, UserAddressDto addDto, String addressType) throws UserException {

		Long id = JwtService.parse(token);
		Users user = repository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		UserAddress address = userAddressrepository.findaddressbyType(addressType, id);
if(address!=null) {
		BeanUtils.copyProperties(addDto, address);
		repository.save(user);
		return address;
}
else{
			return null;
		}
	}
	public UserAddress getByAddressType(String addressType, String token) throws UserException {

		Long id = JwtService.parse(token);
		Users user = repository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		UserAddress address = userAddressrepository.findaddressbyType(addressType, id);
		return address;
	}

	@Override
	@Transactional
	public Users addProfile(MultipartFile file, String token)
			throws AmazonServiceException, SdkClientException, IOException, UserException, S3BucketException {
		Long id = JwtService.parse(token);
		
		Users user = repository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		if (user != null) {
			String profile = s3.uploadFileToS3Bucket(file);
			user.setProfile(profile);
			repository.save(user);
		}
		return null;
	}

	@Override
	@Transactional
	public Users getUserById(String token) throws UserException {
		Long id = JwtService.parse(token);
		Users user = repository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		return user;
	}
	@Override
	@Transactional
	public Users removeProfile(String token) throws UserException, S3BucketException {
		Long id = JwtService.parse(token);		
		Users user = repository.findbyId(id).orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));
		if (user != null) {
			String url=user.getProfile();
			 s3.deleteFileFromS3Bucket(url);
			user.setProfile(null);
			repository.save(user);
		}
		return null;
	}

}