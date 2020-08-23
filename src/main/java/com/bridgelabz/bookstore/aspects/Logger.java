package com.bridgelabz.bookstore.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Logger {

	@Before("execution(* com.bridgelabz.bookstore.serviceimplemantation.UserServiceImplementation.userLogin(*))")
	public void beforeUserLoginMethod(JoinPoint joinPoint) throws Throwable {

		log.info("Before User login");
		log.info("method invoked: " + joinPoint.getSignature().getName() + " Logged-in user:" + joinPoint.getArgs()[0]);
	}

	@AfterReturning("execution(* com.bridgelabz.bookstore.serviceimplemantation.UserServiceImplementation.userLogin(*))")
	public void afterUserLoginMethod() {
		log.info("User Login Executed");
	}

	@Before("execution(* com.bridgelabz.bookstore.serviceimplemantation.SellerServiceImplementation.login(*))")
	public void beforeSellerLoginMethod(JoinPoint joinPoint) throws Throwable {

		log.info("Before Seller Login");
		log.info("method invoked: " + joinPoint.getSignature().getName() + " Logged-in seller:"
				+ joinPoint.getArgs()[0]);
	}

	@AfterReturning("execution(* com.bridgelabz.bookstore.serviceimplemantation.SellerServiceImplementation.login(*))")
	public void afterSellerLoginMethod() {
		log.info("Seller Login Executed");
	}

	@Before("execution(* com.bridgelabz.bookstore.serviceimplemantation.AdminServiceImplementation.loginToAdmin(*))")
	public void beforeAdminLoginMethod(JoinPoint joinPoint) throws Throwable {

		log.info("Before Admin Login");
		log.info(
				"method invoked: " + joinPoint.getSignature().getName() + " Logged-in Admin:" + joinPoint.getArgs()[0]);
	}

	@AfterReturning("execution(* com.bridgelabz.bookstore.serviceimplemantation.AdminServiceImplementation.loginToAdmin(*))")
	public void afterAdminLoginMethod() {
		log.info("Admin Login Executed");
	}

	@Around("execution(* com.bridgelabz.bookstore.*.*.*(*)) && !execution(* com.bridgelabz.bookstore.serviceimplemantation.UserServiceImplementation.userLogin(*)) "
			+ "&& !execution(* com.bridgelabz.bookstore.serviceimplemantation.SellerServiceImplementation.login(*)) "
			+ "&& !execution(* com.bridgelabz.bookstore.serviceimplemantation.UserServiceImplementation.loginToAdmin(*))")
	public Object forAllMethods(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Before " + joinPoint.getSignature().getName() + "method of "
				+ joinPoint.getSignature().getDeclaringType().getSimpleName());
		Object val = joinPoint.proceed();
		log.info("After " + joinPoint.getSignature().getName() + " method of class"
				+ joinPoint.getSignature().getDeclaringType().getSimpleName());
		return val;

	}

}
