package com.bridgelabz.bookstore.serviceimplemantation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bridgelabz.bookstore.dto.BookDto;
import com.bridgelabz.bookstore.dto.ReviewDto;
import com.bridgelabz.bookstore.entity.Admin;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.CartDetails;
import com.bridgelabz.bookstore.entity.Order;
import com.bridgelabz.bookstore.entity.Reviews;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.exception.AdminException;
import com.bridgelabz.bookstore.exception.BookException;
import com.bridgelabz.bookstore.exception.ExceptionMessages;
import com.bridgelabz.bookstore.exception.S3BucketException;
import com.bridgelabz.bookstore.exception.SellerException;
import com.bridgelabz.bookstore.exception.UserException;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.repository.ReviewRepository;
import com.bridgelabz.bookstore.repository.SellerRepository;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.service.BookService;
import com.bridgelabz.bookstore.service.ElasticSearchService;
import com.bridgelabz.bookstore.utility.AwsS3Access;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.MailService;

@Service
public class BookServiceImplementation implements BookService {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ReviewRepository reviwRepository;
	@Autowired
	private ElasticSearchService elasticService;
	@Autowired
	ModelMapper mapper;
	@Autowired
	AwsS3Access s3;

	@Override
	@Transactional
	public List<Book> displayBooks(Integer page) throws BookException {
		page = (page - 1) * 12;
		List<Book> books = bookRepository.getAllBooks(page);
		return books;
	}

	@Override
	@Transactional
	public Integer getCountOfBooks() {
		Integer count = bookRepository.getTotalCount();
		return count;
	}

	@Override
	@Transactional
	public Book displaySingleBook(Long id) throws BookException {
		Book book = bookRepository.getBookById(id)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "Book not Found Exception"));
		return book;
	}

	@Override
	@Transactional
	public List<Book> sortByPriceAsc(Integer page) throws BookException {
		page = (page - 1) * 12;
		List<Book> books = bookRepository.getByPriceAsc(page);
		return books;
	}

	@Override
	@Transactional
	public List<Book> sortByPriceDesc(Integer page) throws BookException {
		page = (page - 1) * 12;
		List<Book> books = bookRepository.getByPriceDesc(page);
		return books;
	}

	@Override
	@Transactional
	public List<Book> sortByNewest(Integer page) throws BookException {
		page = (page - 1) * 12;
		List<Book> books = bookRepository.getByDateTime(page);
		return books;
	}

	@Override
	@Transactional
	public Book addBook(String token, BookDto dto) throws SellerException, S3BucketException, IOException {
		Book book = new Book();
		Long id = JwtService.parse(token);
		Seller seller = sellerRepository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, "Seller is not exist"));

		if (seller.isVerified() == true) {
			book = mapper.map(dto, Book.class);
			book.setBookCreatedAt(LocalDateTime.now());
			book.setApproveStatus("Hold");
			seller.getSellerBooks().add(book);
			Book book1 = bookRepository.save(book);
			// MailService.sendEmailToAdmin(seller.getEmail(), book);
			try {
				elasticService.addBook(book1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return book;
		} else {
			throw new SellerException(HttpStatus.NOT_FOUND, "not a verified seller ");
		}
	}

	@Override
	@Transactional
	public Book updateBook(String token, Long bookId, BookDto dto) throws Exception {
		Long id = JwtService.parse(token);
		sellerRepository.getSellerById(id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, "Seller is not exist"));
		Book book = bookRepository.getBookBysellerId(bookId, id)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book is not exist exist to update"));
		book.setBookAuthor(dto.getBookAuthor());
		book.setBookDescription(dto.getBookDescription());
		book.setBookName(dto.getBookName());
		book.setBookPrice(dto.getBookPrice());
		book.setNoOfBooks(dto.getNoOfBooks());
		book.setBookCreatedAt(LocalDateTime.now());
		bookRepository.saveAndFlush(book);

		try {
			elasticService.updateBook(book);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return book;
	}

	@Override
	@Transactional
	public List<Reviews> writeReviewAndRating(String token, ReviewDto review, Long bookId)
			throws UserException, BookException {
		Long id = JwtService.parse(token);
		int flag = 0;
		Users user = userRepository.findById(id)
				.orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND, "Please Verify Email Before Login"));
		Book books = bookRepository.findById(bookId)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book is not exist exist to update"));
		if (user.getOrderBookDetails().isEmpty()) {
			throw new BookException(HttpStatus.NOT_FOUND,
					"Please purchase this book first then and rating and reviews");
		} else if (user.getOrderBookDetails() != null) {
			for (Order order : user.getOrderBookDetails()) {
				for (CartDetails cartdetails : order.getBookDetails()) {
					Book book = cartdetails.getBooks();
					if (book.getBookId() == bookId) {
						flag = 1;
						break;
					}
				}
			}
		} else {
			throw new BookException(HttpStatus.NOT_FOUND,
					"Please purchase this book first then and rating and reviews");
		}
		if (flag == 1) {
			Reviews reviewdetails = new Reviews();
			reviewdetails = mapper.map(review, Reviews.class);
			reviewdetails.setReviewedBy(user.getName());
			reviewdetails.setCreatedAt(LocalDateTime.now());
			books.getReviewRating().add(reviewdetails);		
			bookRepository.save(books);
			List<Reviews> reviewss = books.getReviewRating();
			int ratings = 0;
			int size = reviewss.size();
			for (Reviews rating : books.getReviewRating()) {
				System.out.println(rating);
				ratings += rating.getRating();
			}
			double sum = (double) ratings / size;
			books.setAvgRate(sum);
			bookRepository.save(books);
		} else {
			throw new BookException(HttpStatus.BAD_REQUEST, "book is not purchased by user");
		}
		return books.getReviewRating();
	}

	@Override
	@Transactional
	public List<Reviews> getRatingsOfBook(Long bookId) {
		Book book = null;
		try {
			book = bookRepository.findById(bookId)
					.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book is not  exist to update"));
		} catch (BookException e) {
			e.printStackTrace();
		}
		List<Reviews> reviewss = book.getReviewRating();
		return reviewss;

	}

	@Override
	@Transactional
	public Book removeProfile(String token, Long bookId) throws BookException, S3BucketException {
		Long id = JwtService.parse(token);
		Book book = bookRepository.getBookBysellerId(bookId, id)
				.orElseThrow(() -> new SellerException(HttpStatus.NOT_FOUND, "book not found"));
		if (book != null) {
			String url = book.getBookName();
			s3.deleteFileFromS3Bucket(url);
			book.setBookimage(null);

			bookRepository.save(book);
		}
		return null;
	}

	@Override
	@Transactional
	public List<Book> getEachApprovedBook(String token) throws UserException {
		Long id = JwtService.parse(token);

		Users userInfo = userRepository.findbyId(id).orElseThrow(
				() -> new UserException(HttpStatus.NOT_FOUND, ExceptionMessages.USER_NOT_FOUND_EXCEPTION_MESSAGE));

		List<Book> result = bookRepository.getAllAprrovedBooks();
		return result;
	}

	@Override
	@Transactional
	public Book addProfile(Long id, MultipartFile file) throws BookException, S3BucketException, IOException {
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new BookException(HttpStatus.NOT_FOUND, "book not found"));
		if (book != null) {
			String bookimage = s3.uploadFileToS3Bucket(file);
			book.setBookimage(bookimage);
			bookRepository.save(book);
		}
		return book;
	}
}
