package com.bridgelabz.bookstore.serviceimplemantation;

import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.configuration.Constants;
import com.bridgelabz.bookstore.dto.AdimRestPassword;
import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.AdminPasswordDto;
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
import com.bridgelabz.bookstore.repository.AdminRepository;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.response.MailingandResponseOperation;
import com.bridgelabz.bookstore.service.AdminService;
import com.bridgelabz.bookstore.utility.AwsS3Access;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.JwtService.Token;
import com.bridgelabz.bookstore.utility.MailService;

@Service
public class AdminServiceImplementation implements AdminService {

	@Autowired
	private MailingandResponseOperation response;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncryption;

	@Autowired
	AwsS3Access s3;

	@Transactional
	@Override
	public Admin adminRegistartion(RegisterDto adminInformation) throws AdminException {
		Admin adminInfo = new Admin();
		if (adminRepository.getAdmin(adminInformation.getEmail()).isPresent()) {
			throw new AdminException(HttpStatus.NOT_ACCEPTABLE, ExceptionMessages.EMAIL_ID_ALREADY_PRASENT);
		}

		BeanUtils.copyProperties(adminInformation, adminInfo);
		adminInfo.setPassword((passwordEncryption.encode(adminInformation.getPassword())));
		adminInfo = adminRepository.save(adminInfo);
		String mailResponse = response.fromMessage(Constants.VERIFICATION_LINK,
				JwtService.generateToken(adminInfo.getAdminId(), Token.WITH_EXPIRE_TIME));
		MailService.sendEmail(adminInformation.getEmail(), Constants.VERIFICATION_MSG, mailResponse);
		return adminInfo;
	}

	@Transactional
	@Override
	public boolean verifyAdmin(String token) throws AdminException {
		Long id = null;
		id = JwtService.parse(token);
		Admin admininfomation = adminRepository.getAdminById(id)
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		if (admininfomation.isIsverified()) {
			throw new AdminException(HttpStatus.ALREADY_REPORTED, ExceptionMessages.ALREADY_VERIFIED_EMAIL);
		}
		return adminRepository.verify(id);
	}
	@Transactional
	@Override
	public Admin loginToAdmin(LoginDto information) throws AdminException {
		Admin user = adminRepository.getAdmin(information.getEmail())
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));

		if ((user.isIsverified() == true) && (passwordEncryption.matches(information.getPassword(), user.getPassword())) != true) {

			
			throw new AdminException(HttpStatus.ACCEPTED, "please enter correct email and password ");

		}
		return user;
	}

	@Transactional
	@Override
	public Admin forgetPassword(String email) throws AdminException {

		Admin adminUser = adminRepository.getAdmin(email)
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		if (adminUser.isIsverified() == true) {
			String mailResponse = Constants.REST_LINK
					+ JwtService.generateToken(adminUser.getAdminId(), Token.WITH_EXPIRE_TIME);
			MailService.sendEmail(email, Constants.RSET_PASSWORD, mailResponse);
			//String mailResponse = response.fromMessage(Constants.REST_LINK, "resetpassword");
		//	MailService.sendEmail(adminUser.getEmail(), Constants.RSET_PASSWORD, mailResponse);
			return adminUser;
		}
		return adminUser;

	}

	@Override
	@Transactional
	public boolean resetPassword(ResetPassword update,String token) throws AdminException {

		Long id = JwtService.parse(token);
		Admin admin = adminRepository.getAdminById(id).orElseThrow(
				() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		if (admin.isIsverified()) {
			admin.setPassword(new BCryptPasswordEncoder().encode(update.getConfirmPassword()));
			adminRepository.restAdminPassword(passwordEncryption.encode(update.getConfirmPassword()), id);
		}
		return true;
	}
	@Transactional
	@Override
	public boolean updatepassword(AdminPasswordDto information, String token) throws AdminException {
		Long id = null;
		boolean passwordupdateflag = false;
		id = JwtService.parse(token);
		Admin userinfo = adminRepository.getAdminById(id)
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		if (passwordEncryption.matches(information.getOldpassword(), userinfo.getPassword()) != true) {
			throw new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG);
		}
		information.setConfirmPassword(passwordEncryption.encode(information.getConfirmPassword()));
		adminRepository.upDateAdminPassword(information, id);
		return passwordupdateflag;
	}

	@Transactional
	@Override
	public boolean approveBook(Long BookId,String approveStatus) throws BookException {	
		boolean value;
		bookRepository.getBookById(BookId)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, ExceptionMessages.BOOK_NOT_FOUND));
		if(approveStatus.equals(Constants.HOLD)) {
		 value = false;
		 adminRepository.approvedTheBook(BookId,approveStatus,value);
		 return false;
		}else if(approveStatus.equals(Constants.REJECTED))
		{
			value = false;
			 adminRepository.approvedTheBook(BookId,approveStatus,value);
			 return false;
		}else {
			value = true;
			 adminRepository.approvedTheBook(BookId,approveStatus,value);
			 return true;
		}
		
	}
	@Transactional
	@Override
	public List<Book> getNotapproveBook(String token) throws AdminException {
		Long id = JwtService.parse(token);
		adminRepository.getAdminById(id)
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		return bookRepository.getAllNotAprroveBooks();

	}

	@Override
	public Admin addProfile(MultipartFile file, String token)
			throws AdminException, AmazonServiceException, SdkClientException, IOException, S3BucketException {
		Long id = JwtService.parse(token);
		
		Admin admin = adminRepository.getAdminById(id)
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		if (admin != null) {
			String profile = s3.uploadFileToS3Bucket(file);
			admin.setProfile(profile);
			adminRepository.save(admin);
		}
		return null;
	}
	@Override
	@Transactional
	public Admin getAdminById(String token) throws AdminException {
		Long id = JwtService.parse(token);
		Admin admin = adminRepository.getAdminById(id).orElseThrow(
				() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages. ADMIN_NOT_FOUND_MSG));
		return admin;
	}

	@Override
	public Admin removeProfile(String token) throws AdminException, S3BucketException {
Long id = JwtService.parse(token);
		
		Admin admin = adminRepository.getAdminById(id)
				.orElseThrow(() -> new AdminException(HttpStatus.NOT_FOUND, ExceptionMessages.ADMIN_NOT_FOUND_MSG));
		if (admin != null) {
			String url=admin.getProfile();
			 s3.deleteFileFromS3Bucket(url);
			admin.setProfile(null);
			adminRepository.save(admin);
		}
		return null;
	}

}
