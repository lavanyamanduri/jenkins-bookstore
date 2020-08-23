package com.bridgelabz.bookstore.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.bridgelabz.bookstore.entity.Order;

@Repository
public class OrderRepository {	
	@PersistenceContext
	private EntityManager entityManager;	
	
	public Order save(Order orderInfo) {
		Session session = entityManager.unwrap(Session.class);
		session.saveOrUpdate(orderInfo);
		return orderInfo;
	}
}
