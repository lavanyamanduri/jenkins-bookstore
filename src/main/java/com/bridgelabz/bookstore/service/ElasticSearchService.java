package com.bridgelabz.bookstore.service;

import java.io.IOException;
import java.util.List;

import com.bridgelabz.bookstore.entity.Book;

public interface ElasticSearchService {

	public String addBook(Book book) throws IOException;

	public String updateBook(Book book) throws Exception;

	public Book findById(String id) throws Exception;

	public List<Book> getBookByTitleAndAuthor(String text);
}
