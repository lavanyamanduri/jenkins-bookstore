package com.bridgelabz.bookstore.service;



import java.util.List;
import java.util.Map;

import com.bridgelabz.bookstore.entity.Order;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.UserException;

public interface OrderService {
	public Order orderTheBooks(String token,double total,double deliveryCharge,String adressType) throws UserException ,BookException;
	public List<Order> orderDetails(String token) throws UserException;

}
