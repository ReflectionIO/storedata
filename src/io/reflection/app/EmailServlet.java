//
//  EmailServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.core.Core;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.datatypes.shared.EmailFormatType;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.EmailHelper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class EmailServlet extends HttpServlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action != null) {
			action = action.trim();

			if (action.length() > 0) {
				if ("contact".equals(action)) {
					contact(req, resp);
				} else if ("register".equals(action)) {
					register(req, resp);
				}
			} else {
				resp.getOutputStream().write(0);
				return;
			}
		}

	}

	/**
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String forename = req.getParameter("forename");
		if (forename != null) {
			forename = forename.trim();

			if (forename.length() == 0) {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String surname = req.getParameter("surname");
		if (surname != null) {
			surname = surname.trim();

			if (surname.length() == 0) {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String company = req.getParameter("company");
		if (company != null) {
			company = company.trim();

			if (company.length() == 0) {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String email = req.getParameter("email");
		if (email != null) {
			email = email.trim();

			if (email.length() == 0) {
				resp.getOutputStream().write(0);
				return;
			}
		}

		RegisterUserRequest input = new RegisterUserRequest();

		input.accessCode = "b72b4e32-1062-4cc7-bc6b-52498ee10f09";
		
		input.user = new User();
		input.user.username = email;
		input.user.forename = forename;
		input.user.surname = surname;
		input.user.company = company;

		Core core = new Core();
		RegisterUserResponse output = core.registerUser(input);

		if (output.status == StatusType.StatusTypeSuccess) {

//			String emailTo = input.user.username;
//
//			String name = input.user.forename + " " + surname;
//
//			String body = "Hi "
//					+ forename
//					+ ",\r\n\r\n"
//					+ "We have received your request to be invited to our beta starting early 2014.\r\n\r\n"
//					+ "We are working hard to create a functional, relevant and beautiful service for app market intelligence. We can't wait to share it with you.\r\n\r\n"
//					+ "Stay tuned,\r\n\r\n" + "The Reflection Team\r\n" + "testenv1.reflection.io";
//
//			if (EmailHelper.sendEmail("hello@reflection.io", emailTo, name, "Thank you", body, EmailFormatType.EmailFormatTypePlainText)) {
				resp.getOutputStream().write(1);
//			} else {
//				resp.getOutputStream().write(0);
//			}
		} else {
			resp.getOutputStream().write(0);
		}

	}

	/**
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void contact(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter("name");
		if (name != null) {
			name = name.trim();

			if (name.length() == 0) {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String email = req.getParameter("email");
		if (email != null) {
			email = email.trim();

			if (email.length() == 0) {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String message = req.getParameter("message");
		if (message != null) {
			message = message.trim();

			if (message.length() > 0) {
				message = stripslashes(message);
			} else {
				resp.getOutputStream().write(0);
				return;
			}
		}

		String emailTo = "hello@reflection.io";

		String subject = "Contact Form Submission from " + name;

		String body = "Name: " + name + " \r\nEmail: " + email + "\r\nMessage: " + message + "\r\n";

		if (EmailHelper.sendEmail("hello@reflection.io (Reflection)", emailTo, "Reflection", subject, body, EmailFormatType.EmailFormatTypePlainText)) {
			resp.getOutputStream().write(1);
		} else {
			resp.getOutputStream().write(0);
		}
	}
}
