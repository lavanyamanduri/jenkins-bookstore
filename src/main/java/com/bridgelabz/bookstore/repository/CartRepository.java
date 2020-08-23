package com.bridgelabz.bookstore.repository;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.bridgelabz.bookstore.entity.CartDetails;


@Repository
public class CartRepository {
	@PersistenceContext
	private EntityManager manager;
	
@SuppressWarnings("unchecked")
public boolean findbyuserId( Long userId) {
		Session session = manager.unwrap(Session.class);
	Query q= session.createQuery("FROM users_books_cart where users_user_id=:id ");
		q.setParameter("id", userId);
		
		int status=q.executeUpdate();
		if(status>0) {
			return true;
		}
				
return false;
	}

@SuppressWarnings("unchecked")

public Optional<CartDetails> getCart(Long id) {
	Session session = manager.unwrap(Session.class);
	return session.createQuery("FROM CartDetails where cartId =:id").setParameter("id", id).uniqueResultOptional();

}
	}


