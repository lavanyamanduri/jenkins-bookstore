package com.bridgelabz.bookstore.utility;

import org.springframework.stereotype.Component;

@Component
public class JMSReceiver
{
	public void receiveMessage(MailService Product)
	{
		System.out.println("Received <" + Product + ">");
	}
}