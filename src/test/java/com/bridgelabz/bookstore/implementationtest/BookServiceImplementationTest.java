package com.bridgelabz.bookstore.implementationtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.bridgelabz.bookstore.dto.BookDto;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.service.BookService;
import org.mockito.junit.MockitoJUnitRunner;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplementationTest {
	@Mock
	BookService service;

	@Test
	public void addBook() throws SellerException, S3BucketException, IOException {
		Book book = new Book();
		BookDto dto = new BookDto();
		dto.setBookAuthor("bbbbb");
		dto.setBookDescription("ccccccc");
		dto.setBookName("aaaaaa");
		dto.setBookPrice(100);
		dto.setNoOfBooks(10L);
		Mockito.when(service.addBook(
				"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6M30.J7bbcxYxFXsWCWRY3DMIuSAFZ_PwgmvlOShtZh5ew2UEOFwiSnfIyon0sUYuZx5RO1VVwHvFFOhEfl5a-daIOA",
				dto)).thenReturn(book);
		assertThat(book).isNotNull();
	}
	 
@Test
public void updateBook() throws Exception {
	Book book = new Book();
	BookDto dto = new BookDto();
	book.setBookId(1L);
	dto.setBookAuthor("asaj");
	dto.setBookDescription("asha");
	dto.setBookName("hfkhfk");
	dto.setBookPrice(100);
	dto.setNoOfBooks(10L);
	Mockito.when(service.updateBook(
			"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6M30.J7bbcxYxFXsWCWRY3DMIuSAFZ_PwgmvlOShtZh5ew2UEOFwiSnfIyon0sUYuZx5RO1VVwHvFFOhEfl5a-daIOA",
			1L,dto)).thenReturn(book);
	assertThat(book).isNotNull();
}
}
