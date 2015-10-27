//
//  DBConnectionPoolingFilter.java
//  storedata
//
//  Created by mamin on 10 May 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;

/**
 * @author mamin
 *
 */
public class DBConnectionPoolingFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(DBConnectionPoolingFilter.class.getName());

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		final String requestURI = ((HttpServletRequest) req).getRequestURI();

		boolean logRequest = shouldRequestBeLogged(requestURI);

		if (logRequest && LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "DB Connection pooling filter running on chain for uri: " + requestURI);
		}
		try {
			chain.doFilter(req, resp);
		} catch (final Exception e) {
			throw e;
		} finally {
			DatabaseServiceProvider.provide().realDisconnect();
		}
		if (logRequest && LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "DB Connection pooling exiting chain for uri: " + requestURI);
		}
	}

	/**
	 * @param requestURI
	 * @return
	 */
	private boolean shouldRequestBeLogged(String requestURI) {
		return !requestURI.equals("/ps");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
