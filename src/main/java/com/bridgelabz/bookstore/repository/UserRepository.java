package com.bridgelabz.bookstore.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.entity.Users;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<Users, Long> {

	@Query(value = "insert into Users ( name,password,email,mobileNumber,creationTime,isverified) values (?,?,?,?,?,?)", nativeQuery = true)
	void insertData(String name, String password, String email, String mobileNumber, LocalDateTime creationTime,
			boolean isverified);

	@Query(value = "select * from Users where email=?", nativeQuery = true)
	Optional<Users> FindByEmail(String email);

	@Query(value = "select * from Users where email=?", nativeQuery = true)
	Users checkByEmail(ResetPassword email);

	@Modifying
	@Transactional
	@Query(value = "update Users set password=? where user_id=?", nativeQuery = true)
	void updateUserPassword(String password, Long id);

	@Query(value = "select * from Users where user_id=?", nativeQuery = true)
	Optional<Users> findbyId(Long userId);

	@Query(value = "update Users set isverified = true where user_id = ?", nativeQuery = true)
	void updateIsVerified(Long id);

	@Query(value = "select * from Users where email=?", nativeQuery = true)
	Users FindUserByEmail(String email);
	
	
}
