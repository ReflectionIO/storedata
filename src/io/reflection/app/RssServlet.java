//
//  RssServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.service.ContextAwareServlet;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("serial")
public class RssServlet extends ContextAwareServlet {

	private static final Logger LOG = Logger.getLogger(RssServlet.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doGet()
	 */
	@Override
	protected void doGet() throws ServletException, IOException {

		LOG.info("Cannot rss any thing yet");

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
