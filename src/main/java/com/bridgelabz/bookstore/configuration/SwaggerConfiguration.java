package com.bridgelabz.bookstore.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;

@EnableSwagger2 
@Configuration
public class SwaggerConfiguration {
	/**
	 * @author sachin viraktamath 
	 * SwaggerConfiguration class contain swagger configuration
	 *         
	

	 *        
	 */
	@Bean
	public Docket api() {

		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.bridgelabz.bookstore.controller")).paths(PathSelectors.any())
				.build().apiInfo(metaData());
	}
	private ApiInfo metaData() {
		   Contact contact=new Contact("Amruth Sagar",
		            "https://github.com/SachinVViraktamath/BookstoreApi-s.git","amrutha.sagar@bridgelabz.com");

		    return new ApiInfoBuilder()
		            .title("Book Store Application")
		            .description("Spring boot application for Book store application")
		            .contact(contact)		          
		            .build();
	    }
}