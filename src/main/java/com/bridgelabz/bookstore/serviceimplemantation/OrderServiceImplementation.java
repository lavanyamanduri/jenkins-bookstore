package com.bridgelabz.bookstore.serviceimplemantation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bridgelabz.bookstore.configuration.Constants;
import com.bridgelabz.bookstore.entity.CartDetails;
import com.bridgelabz.bookstore.entity.Order;
import com.bridgelabz.bookstore.entity.UserAddress;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.service.OrderService;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.MailService;


@Service
public class OrderServiceImplementation implements OrderService {

	@Autowired
	private UserRepository userRepository;


	@Transactional
	@Override
	public Order orderTheBooks(String token, double total, double deliveryCharge, String adressType)
			throws UserException, BookException {

		Long id = JwtService.parse(token);
		Random random = new Random();
		List<CartDetails> CartBookItems = new ArrayList<CartDetails>();
		 long orderId;
		 long  bookorderId;
		Order orderDetails = new Order();
		Users userInfo = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user does not exist"));
		UserAddress address = userInfo.getAddress().stream().filter((add) -> add.getAddressType().equals(adressType))
				.findFirst().orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "This address is not exist"));
		double totalAmount = (total + deliveryCharge);
		for (CartDetails cartItems : userInfo.getBooksCart()) {
			CartBookItems.add(cartItems);
		}
		userInfo.getBooksCart().removeAll(CartBookItems);
		orderId = random.nextInt(1000000);

		if (orderId < 0) {
			orderId = orderId * -1;
		
		}
		bookorderId=orderId;
		orderDetails.setOrderPlaceTime(LocalDateTime.now());
		orderDetails.setOrderStatus("success");
		orderDetails.setOrderTackingId(orderId);
		orderDetails.setAddress(address);
		orderDetails.setTotalCost(totalAmount);
		orderDetails.setBookDetails(CartBookItems);
		userInfo.getOrderBookDetails().add(orderDetails);
		userRepository.save(userInfo);		
		Users userInfom = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user does not exist"));	
			
		Order orderdetails = userInfom.getOrderBookDetails().stream().filter((ordr) -> ordr.getOrderTackingId() == bookorderId)
				.findFirst().orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND,"order details is not found"));
		
		String mailResponse = "Ordered book has been done successfully at "+orderdetails.getOrderPlaceTime()+" to "+orderdetails.getAddress().getAddress()+
				"\n  The total amount of the book is Rs."+orderDetails.getTotalCost()+
				"\n";
		MailService.sendEmailTemplate(userInfom.getEmail(), Constants.USER_EMAIL_TEMPLATE, mailResponse);				
		return orderdetails;

	}
	
	@Transactional
	@Override
	public List<Order> orderDetails(String token) throws UserException{
		Long id = JwtService.parse(token);
		Users userInfo = userRepository.findbyId(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "user does not exist"));
		
		return userInfo.getOrderBookDetails();
	
		
	}
 
}