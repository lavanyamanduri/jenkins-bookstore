package com.bridgelabz.bookstore.serviceimplemantation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.configuration.Constants;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.repository.BookQuantityRepository;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.repository.SellerRepository;
import com.bridgelabz.bookstore.service.SellerService;
import com.bridgelabz.bookstore.utility.AwsS3Access;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.JwtService.Token;
import com.bridgelabz.bookstore.utility.MailService;

@Service
public class SellerServiceImplementation implements SellerService {

	@Autowired
	SellerRepository repository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	BookQuantityRepository quantityrepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	AwsS3Access s3;

	@Override
	@Transactional
	public Seller register(RegisterDto dto) {
		Seller seller = new Seller();
		if (repository.getSeller(dto.getEmail()).isPresent()) {
			throw new SellerException(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.SELLER_ALREADY_MSG);
		}
		seller = mapper.map(dto, Seller.class);
		seller.setPassword(encoder.encode(dto.getPassword()));
		seller.setDateTime(LocalDateTime.now());
		repository.save(seller);
		String mailResponse = Constants.SELLER_VERIFICATION_LINK
				+ JwtService.generateToken(seller.getSellerId(), Token.WITH_EXPIRE_TIME);
		MailService.sendEmail(dto.getEmail(), Constants.SELLER_VERIFICATION_MSG, mailResponse);

		return seller;
	}

	@Transactional
	@Override
	public Seller login(LoginDto dto) {
		Seller seller = repository.getSeller(dto.getEmail())
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, ExceptionMessages.SELLER_NOT_FOUND_MSG));

		if ((seller.isVerified() == true) && (encoder.matches(dto.getPassword(), seller.getPassword())) != true) {

			String mailResponse = Constants.SELLER_VERIFICATION_LINK
					+ JwtService.generateToken(seller.getSellerId(), Token.WITH_EXPIRE_TIME);
			MailService.sendEmail(dto.getEmail(), Constants.SELLER_VERIFICATION_MSG, mailResponse);

			throw new SellerException(HttpStatus.ACCEPTED, ExceptionMessages.SELLER_VRFIED_YOUR_EMAIL);

		}

		return seller;

	}

	@Override
	@Transactional
	public Boolean verify(String token) {
		Long id = (Long) JwtService.parse(token);
		Seller seller = repository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.BAD_REQUEST, ExceptionMessages.SELLER_NOT_FOUND_MSG));
		if (repository.verify(id) != true)
			throw new SellerException(HttpStatus.BAD_REQUEST, ExceptionMessages.SELLER_ALREADY_VRFIED);

		return true;

	}
@Override
@Transactional
	public Seller forgetPassword(String email) {
		Seller seller = repository.getSeller(email)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, ExceptionMessages.SELLER_NOT_FOUND_MSG));
		if (seller.isVerified() != true) {
			throw new SellerException(HttpStatus.BAD_REQUEST, ExceptionMessages.SELLER_VRIFICATION_FAIL_MSG);
		}
		String mailResponse = Constants.SELLER_RESETPASSWORD_LINK
				+ JwtService.generateToken(seller.getSellerId(), Token.WITH_EXPIRE_TIME);
		MailService.sendEmail(email, Constants.SELLER_VERIFICATION_MSG, mailResponse);
		return seller;
	}

	@Override
	@Transactional
	public Boolean resetPassword(ResetPassword update, String token) {
		Long id = JwtService.parse(token);
		Seller seller = repository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, ExceptionMessages.SELLER_NOT_FOUND_MSG));

		update.setConfirmPassword(encoder.encode(update.getConfirmPassword()));
		repository.save(seller);

		return true;
	}

	@Override
	@Transactional
	public Seller addProfile(MultipartFile file, String token) throws SellerException, AmazonServiceException,
			SdkClientException, IOException, SellerException, S3BucketException {
		Long id = JwtService.parse(token);
		
		Seller seller = repository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, ExceptionMessages.SELLER_NOT_FOUND_MSG));
		if (seller != null) {
			String profile = s3.uploadFileToS3Bucket(file);
			seller.setProfile(profile);
			repository.save(seller);
		}
		return null;
	}

	@Override
	@Transactional
	public Seller getSellerById(String token) throws SellerException {
		Long id = JwtService.parse(token);
		Seller seller = repository.getSellerById(id).orElseThrow(
				() -> new SellerException(HttpStatus.NOT_FOUND, ExceptionMessages.SELLER_NOT_FOUND_MSG));
		return seller;
	}
	
	@Override
	@Transactional
	public Seller removeProfile( String token) throws SellerException, S3BucketException {
		Long id = JwtService.parse(token);
		
		Seller seller = repository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, ExceptionMessages.SELLER_NOT_FOUND_MSG));
		if (seller != null) {
			String url=seller.getProfile();
			s3.deleteFileFromS3Bucket(url);
			seller.setProfile(null);
			repository.save(seller);
		}
		return null;
	}
	
	@Transactional
	@Override
	public List<Book> getBooks(String token) throws SellerException{	
		Long id = JwtService.parse(token);	
		Seller seller = repository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, "No books added by seller"));
	
		return seller.getSellerBooks();
	}


}