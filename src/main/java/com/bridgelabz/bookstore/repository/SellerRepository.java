package com.bridgelabz.bookstore.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.dto.SellerPasswordUpdateDto;
import com.bridgelabz.bookstore.entity.Seller;

@Repository
public class SellerRepository {

	@PersistenceContext
	private EntityManager entityManger;

	/* Query for save the data into sellerTable */

	public Seller save(Seller seller) {
		Session session = entityManger.unwrap(Session.class);
		session.saveOrUpdate(seller);
		return seller;
	}

	// * Query to get the seller information by email */
	@SuppressWarnings("unchecked")
	public Optional<Seller> getSeller(String email) {
		Session session = entityManger.unwrap(Session.class);
		return session.createQuery("FROM Seller where email=:email").setParameter("email", email)
				.uniqueResultOptional();

	}

	public boolean isSellerExist(String email) {
		Session session = entityManger.unwrap(Session.class);
		Query<Seller> q = session.createQuery("FROM Seller where email=:email");
		q.setParameter("email", email);
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean verify(Long id) {
		Session session = entityManger.unwrap(Session.class);
		Query<Seller> q = session.createQuery("update Seller set isVerified =:p" + " " + " " + " where sellerId=:i");
		q.setParameter("p", true);
		q.setParameter("i", id);
		int status = q.executeUpdate();
		if (status > 0) {
			return true;

		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean update(ResetPassword update, Long id) {
		Session session = entityManger.unwrap(Session.class);
		Query<Seller> q = session.createQuery("update Seller set password=:p" + " " + " where sellerId=:id");

		q.setParameter(" p", update.getConfirmPassword());
		q.setParameter("id", id);

		int status = q.executeUpdate();
		if (status > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void delete(Seller seller) {
		Session session = entityManger.unwrap(Session.class);
		session.delete(seller);
	}

	@SuppressWarnings("unchecked")
	public List<Seller> getSellers() {
		Session session = entityManger.unwrap(Session.class);
		List<Seller> sellerList = session.createQuery("FROM Seller").getResultList();
		return sellerList;
	}

	@SuppressWarnings("unchecked")
	public Optional<Seller> getSellerById(Long id) {
		Session session = entityManger.unwrap(Session.class);
		return session.createQuery("FROM Seller where sellerId=:id").setParameter("id", id).uniqueResultOptional();

	}

	@SuppressWarnings("rawtypes")
	public boolean addBookBySeller(Long id, Long bookId) {
		Session session = entityManger.unwrap(Session.class);
		Query q = session.createQuery("update Book set sellerId:id,isBookApproved:true" + " " + "where bookId:bookId");
		q.setParameter("id", id);
		q.setParameter("bookId", bookId);
		int addBook = q.executeUpdate();
		if (addBook > 0) {
			return true;
		} else {
			return false;
		}

	}

}
