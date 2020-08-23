package com.bridgelabz.bookstore.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bridgelabz.bookstore.entity.Order;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.serviceimplemantation.OrderServiceImplementation;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private OrderServiceImplementation service;	 

	@GetMapping("/orderdetails")
	public ResponseEntity<Response> orderDetails(@RequestHeader("token")String token)throws UserException
	{
		List<Order> result = service.orderDetails(token);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "Order Successfully done", result));
	}
	
	@PostMapping("/placeorders")
	public ResponseEntity<Response> orderTheBooks(@RequestHeader("token") String token,@RequestParam double total,@RequestParam double deliveryCharge, @RequestParam String addressType) throws UserException, BookException {
	Order result = service.orderTheBooks(token, total, deliveryCharge, addressType);
		return ResponseEntity.ok()
				.body(new Response(HttpStatus.ACCEPTED, "Order Successfully done", result));
	}
}
