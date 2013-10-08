//
//  EmailServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class EmailServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(EmailServlet.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		if (name != null && name.length() > 0) {
			name = name.trim();
		} else {
			resp.getOutputStream().write(0);
			return;
		}

		String email = req.getParameter("email");
		if (email != null && email.length() > 0) {
			email = email.trim();
		} else {
			resp.getOutputStream().write(0);
			return;
		}

		String message = req.getParameter("message");
		if (message != null && message.length() > 0) {
			message = message.trim();

			if (message.length() > 0) {
				message = StringUtils.stripslashes(message);
			} else {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String emailTo = "hello@reflection.io";
		String subject = "Contact Form Submission from " + name;

		String body = "Name: " + name + " \r\nEmail: " + email + "\r\nMessage: " + message + "\r\n\r\nreflection.io";

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailTo, "reflection.io"));
//			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email, name));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo, "reflection.io"));
			msg.setSubject(subject);
			msg.setText(body);
			Transport.send(msg);

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Contact email sent successfully.");
			}

			resp.getOutputStream().write(1);
		} catch (AddressException e) {
			LOG.log(Level.SEVERE, "Error sending email", e);
			resp.getOutputStream().write(0);
		} catch (MessagingException e) {
			LOG.log(Level.SEVERE, "Error sending email", e);
			resp.getOutputStream().write(0);
		}

	}
}
