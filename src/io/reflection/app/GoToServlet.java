//
//  GoToServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.service.ContextAwareServlet;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class GoToServlet extends ContextAwareServlet {

	private static final Logger LOG = Logger.getLogger(GoToServlet.class.getName());

	private static final String TITLE_PREFIX_PLACEHOLDER = "${prefix}";
	private static final int TITLE_PREFIX_PLACEHOLDER_LAST = TITLE_PREFIX_PLACEHOLDER.length();

	private static final String VARIABLES_PLACEHOLDER = "${variables}";
	private static final int VARIABLES_PLACEHOLDER_LAST = VARIABLES_PLACEHOLDER.length();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doGet()
	 */
	@Override
	protected void doGet() throws ServletException, IOException {
		String output = "";
		if ("".equals(REQUEST.get().getRequestURI()) || "/".equals(REQUEST.get().getRequestURI()) || REQUEST.get().getRequestURI().startsWith("/index.html")) {
			StringBuffer indexPage = new StringBuffer();

			InputStream is = GoToServlet.class.getResourceAsStream("res/index.html");

			byte[] buffer = new byte[1024];
			int read;
			while ((read = is.read(buffer)) > 0) {
				indexPage.append(new String(buffer, 0, read));
			}

			int titleIndex = indexPage.indexOf(TITLE_PREFIX_PLACEHOLDER);

			String titlePrefix;
			if ((titlePrefix = System.getProperty("site.title.prefix", "")).length() > 0) {
				titlePrefix += " - ";
				indexPage.replace(titleIndex, titleIndex + TITLE_PREFIX_PLACEHOLDER_LAST, titlePrefix);
			}

			StringBuffer variables = new StringBuffer();

			variables.append("<script  type='text/javascript'>\n");

			variables.append("var storesJson = '");
			// add stores here
			variables.append("';\n");

			variables.append("var countriesJson = '");
			// add countries here
			variables.append("';\n");

			if (isLoggedInUser()) {
				variables.append("var userJson = '");
				// add user here -- this should include roles and permissions
				variables.append("';\n");
			}

			variables.append("</script>");

			int variablesIndex = indexPage.indexOf(VARIABLES_PLACEHOLDER);
			indexPage.replace(variablesIndex, variablesIndex + VARIABLES_PLACEHOLDER_LAST, variables.toString());

			output = indexPage.toString();
		} else {
			LOG.info("Request [" + REQUEST.get().getRequestURI() + "] does not match / or index.html");
		}

		RESPONSE.get().getWriter().write(output);
	}

	// uses cookies to determine the logged in user
	private boolean isLoggedInUser() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doPost()
	 */
	@Override
	protected void doPost() throws ServletException, IOException {
		doGet();
	}
}
