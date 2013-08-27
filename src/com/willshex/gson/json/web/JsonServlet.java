//
//  JsonServlet.java
//  jspacecloud
//
//  Created by William Shakour on June 16, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.willshex.gson.json.web;

import static com.spacehopperstudios.utility.StringUtils.urldecode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public abstract class JsonServlet extends HttpServlet {

	private final Class<?> thisClass = getClass();
	private final Logger LOG = Logger.getLogger(thisClass.getName());

	// protected boolean allowXDomainPosting = false;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String output = "null";

		String action = req.getParameter("action");
		String request = req.getParameter("request");

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Action is [" + action + "] and request is [" + request + "]");
		}

		if (action != null && request != null) {
			request = urldecode(request);

			output = processAction(action, (JsonObject) (new JsonParser()).parse(request));

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Output is [" + output + "]");
			}

			// relaced resp.setContentType("application/x-javascript"); from
			// google example
			resp.setContentType("application/json; charset=utf-8");
			resp.setHeader("Cache-Control", "no-cache");

			// if (allowXDomainPosting) {
			// resp.setHeader("Access-Control-Allow-Origin", "*");
			// }

			String acceptEncoding = req.getHeader("Accept-Encoding");
			if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
				resp.setHeader("Content-Encoding", "gzip");

				OutputStreamWriter writer = null;
				try {
					GZIPOutputStream outputStream = new GZIPOutputStream(resp.getOutputStream());
					writer = new OutputStreamWriter(outputStream);
					writer.append(output);
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
			} else {
				resp.getWriter().println(/* "var response=" . */output);
			}
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
		doGet(req, resp);
	};

	protected abstract String processAction(String action, JsonObject request);
}
