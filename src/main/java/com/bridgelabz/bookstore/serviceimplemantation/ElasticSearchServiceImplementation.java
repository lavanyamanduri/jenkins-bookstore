package com.bridgelabz.bookstore.serviceimplemantation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.service.ElasticSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ElasticSearchServiceImplementation implements ElasticSearchService {

	String INDEX = "bookstore";
	String TYPE = "book";

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String addBook(Book book) throws IOException {

		@SuppressWarnings("unchecked")
		Map<String, Object> documentMapper = objectMapper.convertValue(book, Map.class);

		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, String.valueOf(book.getBookId()))
				.source(documentMapper);

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

		return indexResponse.getResult().name();
	}

	@Override
	public String updateBook(Book book) throws Exception {

		Book book1 = findById(String.valueOf(book.getBookId()));

		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, String.valueOf(book1.getBookId()));

		@SuppressWarnings("unchecked")
		Map<String, Object> mapDoc = objectMapper.convertValue(book, Map.class);
		updateRequest.doc(mapDoc);

		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
		return updateResponse.getResult().name();
	}

	@Override
	public Book findById(String id) throws IOException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);

		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		Map<String, Object> resultMap = getResponse.getSource();

		return objectMapper.convertValue(resultMap, Book.class);
	}

	@Override
	public List<Book> getBookByTitleAndAuthor(String text) {

		SearchRequest searchRequest = buildSearchRequest(INDEX, TYPE);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		QueryBuilder query = QueryBuilders.boolQuery()
				.should(QueryBuilders.queryStringQuery(text).lenient(true).field("bookName").field("bookAuthor"))
				.should(QueryBuilders.queryStringQuery("*" + text + "*").lenient(true).field("bookName")
						.field("bookAuthor"));
		searchSourceBuilder.query(query);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getSearchResult(searchResponse);
	}

	private List<Book> getSearchResult(SearchResponse searchResponse) {
		SearchHit[] searchHits = searchResponse.getHits().getHits();
		List<Book> bookDoc = new ArrayList<Book>();
		if (searchHits.length > 0) {
			Arrays.stream(searchHits)
					.forEach(hit -> bookDoc.add(objectMapper.convertValue(hit.getSourceAsMap(), Book.class)));
		}

		return bookDoc;
	}

	private SearchRequest buildSearchRequest(String index, String type) {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(index);
		searchRequest.types(type);
		return searchRequest;
	}

}
