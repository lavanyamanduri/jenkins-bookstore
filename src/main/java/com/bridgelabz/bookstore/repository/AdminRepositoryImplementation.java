package com.bridgelabz.bookstore.repository;


import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.bridgelabz.bookstore.dto.AdminPasswordDto;
import com.bridgelabz.bookstore.entity.Admin;


@Repository
public class AdminRepositoryImplementation implements AdminRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Admin save(Admin admin) {

		Session session = entityManager.unwrap(Session.class);
		session.saveOrUpdate(admin);
		return admin;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Admin> getAdmin(String email) {
		Session session = entityManager.unwrap(Session.class);
		return session.createQuery("FROM Admin where email =:email").setParameter("email", email)
				.uniqueResultOptional();

	}

	@Override
	public boolean verify(Long id) {
		Session session = entityManager.unwrap(Session.class);

		@SuppressWarnings("unchecked")
		TypedQuery<Admin> q = session.createQuery("update Admin set  isverified =:p where adminId=:i")
				.setParameter("p", true).setParameter("i", id);
		int status = q.executeUpdate();
		if (status > 0) {
			return true;
		} else {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Admin> getAdminById(Long id) {
		Session session = entityManager.unwrap(Session.class);
		return session.createQuery("FROM Admin where adminId=:id").setParameter("id", id).uniqueResultOptional();

	}

	@Override
	public boolean upDateAdminPassword(AdminPasswordDto information,Long id) {
		Session session = entityManager.unwrap(Session.class);
		Query q = session.createQuery("update Admin set password =:p" + " " + " " + "where adminId=:i")
				.setParameter("p", information.getConfirmPassword()).setParameter("i", id);
		int status = q.executeUpdate();
		if (status > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public boolean approvedTheBook(Long BookId,String approveStatus,boolean value) {
	//	boolean value = true;
		Session session = entityManager.unwrap(Session.class);
		Query q = session.createQuery("update Book set isBookApproved =:p ,approveStatus=:a" + " " + " " + "where bookId=:i");
		q.setParameter("p", value);
		q.setParameter("i", BookId);
		q.setParameter("a", approveStatus);
		int status = q.executeUpdate();
		if (status > 0) {
			return true;
		} else {
			return false;
		}

	}


	@Override
	public boolean restAdminPassword(String information,Long id) {
	

		Session session = entityManager.unwrap(Session.class);
		Query q = session.createQuery("update Admin set password =:p" + " " + " " + "where id=:id")
				.setParameter("p", information).setParameter("id", id);
		int status = q.executeUpdate();
		if (status > 0) {
			return true;
		} else {
			return false;
		}
	}

}
