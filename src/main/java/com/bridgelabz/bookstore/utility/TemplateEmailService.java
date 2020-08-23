package com.bridgelabz.bookstore.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.bridgelabz.bookstore.entity.Users;
import com.bridgelabz.bookstore.response.MailResponse;
import com.bridgelabz.bookstore.response.Response;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;


@Service
@Component
public class TemplateEmailService {
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private Configuration config;

	public MailResponse sendEmail(Users request, Map<String, Object> model) {
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();
		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			// add attachment
			helper.addAttachment("logo.png", new ClassPathResource("logo.png"));

			Template t = config.getTemplate("email-template.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			
			helper.setTo(request.getEmail());
			helper.setText(html, true);
			sender.send(message);

			response.setMessage("mail send to : " + request.getEmail());
			response.setStatus(Boolean.TRUE);

		} catch (MessagingException | RuntimeException | TemplateException | IOException e) {
			response.setMessage("Mail Sending failure : "+e.getMessage());
			response.setStatus(Boolean.FALSE);
		}

		return response;
	}
	
}
