//
//  EmailHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import io.reflection.app.logging.GaeLevel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Map;
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

/**
 * @author billy1380
 * 
 */
public class EmailHelper {

	private static final Logger LOG = Logger.getLogger(EmailHelper.class.getName());

	public static String inflate(Map<String, ?> values, String templateBody) {
		StringBuffer email = new StringBuffer();

		int count = templateBody.length();

		boolean foundDolar = false, foundStart = false;
		StringBuffer magic = new StringBuffer();
		for (int i = 0; i < count; i++) {
			char c = templateBody.charAt(i);

			if (foundDolar) {
				if (c == '{') {
					foundStart = true;
					magic.setLength(0);
				} else {
					email.append('$');
				}

				foundDolar = false;
			} else if (!foundDolar && c == '$') {
				foundDolar = true;
			} else if (foundStart && c == '}') {
				foundStart = false;

				String[] path = null;
				if (magic.length() > 0) {
					path = magic.toString().split(".");
				}

				Object o = null;
				if (path != null && path.length > 0) {
					o = values.get(path[0]);

					for (int j = 1; j < path.length; j++) {
						o = getPropertyValue(o, path[j]);
					}

					if (o != null) {
						email.append(o.toString());
					}
				}
			} else if (foundStart) {
				magic.append(c);
			} else {
				email.append(c);
			}
		}

		return email.toString();
	}

	public static boolean sendEmail(String from, String to, String name, String subject, String body) {
		boolean sent = false;

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);

			InternetAddress address = new InternetAddress(from, "Reflection");

			msg.setFrom(address);
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, name));

			msg.setSubject(subject);
			msg.setText(body);
			Transport.send(msg);

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Contact email sent successfully.");
			}

			sent = true;
		} catch (AddressException e) {
			LOG.log(Level.SEVERE, "Error sending email", e);
			sent = false;
		} catch (MessagingException e) {
			LOG.log(Level.SEVERE, "Error sending email", e);
			sent = false;
		} catch (UnsupportedEncodingException e) {
			LOG.log(Level.SEVERE, "Error encoding email", e);
			sent = false;
		}

		return sent;
	}

	private static Object getPropertyValue(Object object, String propertyName) {
		Object value = null;
		Field[] fields = object.getClass().getDeclaredFields();

		Field field = null;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			if (field.isAccessible() && propertyName.equals(field.getName())) {
				try {
					value = field.get(object);
				} catch (IllegalArgumentException e) {
					LOG.log(GaeLevel.SEVERE, String.format("Error accessing field [%s]", field.getName()), e);
				} catch (IllegalAccessException e) {
					LOG.log(GaeLevel.SEVERE, String.format("Error accessing field [%s]", field.getName()), e);
				}
			}
		}

		return value;
	}
}
