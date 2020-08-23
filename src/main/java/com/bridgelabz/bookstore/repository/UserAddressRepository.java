package com.bridgelabz.bookstore.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bridgelabz.bookstore.entity.UserAddress;

@Repository
@Transactional
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

	@Query(value = "select * from user_address where address_id=?", nativeQuery = true)
	public Optional<UserAddress> findbyId(long addressId);
	@Query(value = "select * from user_address where address_type=?", nativeQuery = true)
	public Optional<UserAddress> findbyType(String addressType);
	@Query(value = "select * from user_address where address_type=? and user_id=?", nativeQuery = true)
	public UserAddress findaddressbyType(String addressType,Long id);
	@Modifying
	@Query(value = "insert into user_address (landmark,city,locality,address,addressType,pinCode,name,phonenumber) values (?,?,?,?,?,?,?,?)", nativeQuery = true)
	void addAddress(String landmark, String city, String locality, String address, String addressType, int pinCode,
			String name, String phonenumber);

	@Modifying
	@Transactional
	@Query(value = "delete from user_address where address_id = ? and userId = ?", nativeQuery = true)
	void removeAddress(long addressId, long userId);

	@Modifying
	@Transactional
	@Query(value = "update user_address set landmark = ?,city =? ,locality = ?,address = ?, addressType = ?, pincode = ?,name=?,phonenumber=? where userId = ? and addressId = ?", nativeQuery = true)
	void updateAdd(String landmark, String city, String locality, String address, String addressType, int pinCode,
			String name, String phonenumber);
}
