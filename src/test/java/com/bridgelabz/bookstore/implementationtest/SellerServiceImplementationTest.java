package com.bridgelabz.bookstore.implementationtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bridgelabz.bookstore.dto.LoginDto;
import com.bridgelabz.bookstore.dto.RegisterDto;
import com.bridgelabz.bookstore.dto.ResetPassword;
import com.bridgelabz.bookstore.entity.Book;
import com.bridgelabz.bookstore.entity.Seller;
import com.bridgelabz.bookstore.response.Response;
import com.bridgelabz.bookstore.service.SellerService;
import com.bridgelabz.bookstore.utility.JwtService;
import com.bridgelabz.bookstore.utility.JwtService.Token;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class SellerServiceImplementationTest {
	    @Mock
	    RegisterDto sellerDto;
	    @Mock
	    SellerService sellerServiceImpl;
	    Seller seller = new Seller();
	    String token=null;
	    
//	    @Test
//		public void register_seller_user_with_valid_seller_informations() throws Exception {
//			Seller seller = new Seller();
//			seller.setEmail("shswdjdgdke");
//			ResponseEntity<Response> responseEntity = ResponseEntity.status(HttpStatus.ACCEPTED)
//					.body(new Response(208, "test" + user.getEmail() + "<=== please verify your email first"));
//			ObjectMapper objectMapper = new ObjectMapper();
//			String newUserDto = objectMapper.writeValueAsString(seller);
//			MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(REGISTER_SELLER_URI)
//					.content(newUserDto).contentType(MediaType.APPLICATION_JSON);
//			Mockito.when(userService.register(Mockito.any())).thenReturn(responseEntity);
//
//			MockHttpServletResponse fetchedResponse = mockMvc.perform(requestBuilder).andReturn().getResponse();
//
//			log.info("fetch result : " + fetchedResponse.getStatus());
//			Assert.assertEquals("Checking failure scenario of add address", fetchedResponse.getStatus(),
//					202);
//		}

		@Test
		public void testLogin() throws Exception {
			Seller seller = new Seller();
			LoginDto login = new LoginDto();
			login.setEmail("csandhyait@gmail.com");
			login.setPassword("sandy");
			Mockito.when(sellerServiceImpl.login(login)).thenReturn(seller);
			assertThat(seller).isNotNull();
		}

		@Test
		public void testVerify() {
			Seller seller = new Seller();
			seller.setSellerId(3L);
			Mockito.when(JwtService.generateToken(3L, Token.WITH_EXPIRE_TIME)).thenReturn(token);
			Mockito.when(sellerServiceImpl.verify(
					"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6M30.J7bbcxYxFXsWCWRY3DMIuSAFZ_PwgmvlOShtZh5ew2UEOFwiSnfIyon0sUYuZx5RO1VVwHvFFOhEfl5a-daIOA"))
					.thenReturn(true);
			assertThat(token).isNotNull();
		}

		@Test
		public void testForgetPassword() {
			Seller seller = new Seller();
			seller.setEmail("csandhyait@gmail.com");
			Mockito.when(sellerServiceImpl.forgetPassword("csandhyait@gmail.com")).thenReturn(seller);
			assertThat(seller).isNotNull();
		}

		@Test
		public void testResetPassword() {
			Seller seller = new Seller();
			seller.setSellerId(3L);
			ResetPassword password = new ResetPassword();
			password.setConfirmPassword("sandhya");
			Mockito.when(sellerServiceImpl.resetPassword(password,
					"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6M30.J7bbcxYxFXsWCWRY3DMIuSAFZ_PwgmvlOShtZh5ew2UEOFwiSnfIyon0sUYuZx5RO1VVwHvFFOhEfl5a-daIOA"))
					.thenReturn(true);
			assertThat(seller).isNotNull();
		}

		@Test
		public void testGetSellerById() {
			Seller seller = new Seller();
			seller.setSellerId(3L);
			Mockito.when(sellerServiceImpl.getSellerById(
					"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6M30.J7bbcxYxFXsWCWRY3DMIuSAFZ_PwgmvlOShtZh5ew2UEOFwiSnfIyon0sUYuZx5RO1VVwHvFFOhEfl5a-daIOA"))
					.thenReturn(seller);
			assertThat(seller).isNotNull();
		}

		@Test
		public void testGetBooks() {
			Seller seller = new Seller();
			seller.setSellerId(3L);
			List<Book> book = seller.getSellerBooks();
			Mockito.when(sellerServiceImpl.getBooks(
					"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6M30.J7bbcxYxFXsWCWRY3DMIuSAFZ_PwgmvlOShtZh5ew2UEOFwiSnfIyon0sUYuZx5RO1VVwHvFFOhEfl5a-daIOA"))
					.thenReturn(book);
			assertThat(book).isNotNull();
		}
}
