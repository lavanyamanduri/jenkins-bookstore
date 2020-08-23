package com.bridgelabz.bookstore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bridgelabz.bookstore.utility.SpringMail;

@Configuration
public class SpringMailConfig {
	@Bean
	public SpringMail getSpringmail() {
		return new SpringMail();

	}
}