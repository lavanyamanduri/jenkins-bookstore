package com.bridgelabz.bookstore.repository;

import java.util.Optional;

import com.bridgelabz.bookstore.dto.AdminPasswordDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.exception.BookException;

public interface AdminRepository {

	Admin save(Admin adminInfromation);

	public Optional<Admin> getAdmin(String email);

	boolean verify(Long id);

	Optional<Admin> getAdminById(Long id);

	public boolean restAdminPassword(String information, Long id);

	boolean upDateAdminPassword(AdminPasswordDto information,Long id);

	boolean approvedTheBook(Long BookId,String approveStatus,boolean value);

}