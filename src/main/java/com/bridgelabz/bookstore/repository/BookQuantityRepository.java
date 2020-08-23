package com.bridgelabz.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgelabz.bookstore.entity.BookQuantity;

public interface BookQuantityRepository extends JpaRepository<BookQuantity, Long> {

}
