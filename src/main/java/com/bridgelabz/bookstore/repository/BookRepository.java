package com.bridgelabz.bookstore.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bridgelabz.bookstore.entity.Book;

@Repository
@Transactional
public interface BookRepository extends JpaRepository<Book, Long> {


	@Query(value = "select * from book where is_book_approved=true limit ?1,12", nativeQuery = true)
	public List<Book> getAllBooks(Integer page);

	@Query(value = "select * from book where book_id=?1", nativeQuery = true)
	public Optional<Book> getBookById(Long id);
	

	@Query(value = "select * from book where is_book_approved=false", nativeQuery = true)
	public List<Book> getAllNotAprroveBooks();

	@Query(value = "select * from book where is_book_approved=true order by book_price asc limit ?1,12", nativeQuery = true)
	public List<Book> getByPriceAsc(Integer page);

	@Query(value = "select * from book where is_book_approved=true order by book_price desc limit ?1,12", nativeQuery = true)
	public List<Book> getByPriceDesc(Integer page);

	@Query(value = "select * from book where is_book_approved=true order by book_created_at asc limit ?1,12 ", nativeQuery = true)
	public List<Book> getByDateTime(Integer page);

	@Query(value = "select count(*) from book where is_book_approved=true", nativeQuery = true)
	public Integer getTotalCount();

	@Query(value = "select * from book where book_id=? and seller_id=?", nativeQuery = true)
	public Optional<Book> getBookBysellerId(Long id,Long sellerId);
	@Query(value = "select * from book where is_book_approved=true", nativeQuery = true)
	public List<Book> getAllAprrovedBooks();
}
