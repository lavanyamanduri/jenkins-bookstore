package com.bridgelabz.bookstore.configuration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bridgelabz.bookstore.response.MailObject;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RabbitMQSender {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private MailObject mailSender;
	
	@Value("rmq.rube.exchange")
	private String exchange;
	
	@Value("rube.key")
	private String routingkey;	
	
	public void send(MailObject message) {
		log.info("sending the message");
		rabbitTemplate.convertAndSend(exchange, routingkey, message);
		
	}
	
//	public void receive(MailObject message) {
//		
//		rabbitTemplate.convertSendAndReceive(exchange,routingkey, message);
//		log.info("message delivered");
//
//	}
}
