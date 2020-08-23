package com.bridgelabz.bookstore.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgelabz.bookstore.entity.Reviews;

@Transactional
public interface ReviewRepository extends JpaRepository<Reviews, Long>{

}
