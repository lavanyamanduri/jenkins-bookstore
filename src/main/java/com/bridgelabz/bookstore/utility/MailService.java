package com.bridgelabz.bookstore.utility;

import org.springframework.stereotype.Component;

import com.bridgelabz.bookstore.entity.Book;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class MailService {

	/**
	 * Method that is used to Authenticate and send the mail for verification of
	 * seller and user
	 */
	
	public static void sendEmail(String toEmail, String subject, String body) {

		String fromEmail = System.getenv("email");

		String password = System.getenv("password");

		Properties prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}

		};
		Session session = Session.getInstance(prop, auth);
		send(session, fromEmail, toEmail, subject, body);
	}
	
	
	
	public static void sendEmailTemplate(String toEmail, String subject, String body) {

		String fromEmail = System.getenv("email");

		String password = System.getenv("password");

		Properties prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}

		};
		Session session = Session.getInstance(prop, auth);
		send(session, fromEmail, toEmail, subject, body);
	}

	/**
	 * Method that is used to Authenticate and send the mail for verification of
	 * book to admin
	 */

	public static void sendEmailToAdmin(String subject, Book book) {

		String fromEmail = System.getenv("email");

		String password = System.getenv("password");

		String toEmail = System.getenv("emailAdmin");
		String toPassword = System.getenv("passwordAdmin");
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}

		};
		Session session = Session.getInstance(prop, auth);
		sendToAdmin(session, fromEmail, toEmail, subject, book);
	}

	private static void sendToAdmin(Session session, String fromEmail, String toEmail, String subject, Book book) {
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail, "bookstore"));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			book.getBookName();
			book.getBookAuthor();
			book.getBookDescription();
			book.getBookPrice();
			message.setContent(book, "book");

			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured while sending mail");
		}
	}

	/**
	 * Send method is used to dispatch the mail
	 */
	private static void send(Session session, String fromEmail, String toEmail, String subject, String body) {
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail, "bookstore"));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception occured while sending mail");
		}
	}

}