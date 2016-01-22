//
//  AppleReporterHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 23 Dec 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.willshex.gson.json.service.server.InputValidationException;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AppleReporterHelper {
	private static final String						ITUNES_ACCOUNT_KEY							= "Account";
	private static final String						ITUNES_PASSWORD_KEY							= "Password";
	private static final String						ITUNES_USERID_KEY								= "UserId";
	private static final String						ITUNES_MODE_KEY									= "Mode";
	private static final String						ITUNES_FINANCEURL_KEY						= "FinanceUrl";
	private static final String						ITUNES_SALESURL_KEY							= "SalesUrl";

	private static final String						ITUNES_MODE											= "Robot.xml";
	private static final String						ITUNES_FINANCESERVICE_URL				= "https://reportingitc-reporter.apple.com/reportservice/finance/v1";
	private static final String						ITUNES_SALESSERVICE_URL					= "https://reportingitc-reporter.apple.com/reportservice/sales/v1";

	private transient static final Logger	LOG															= Logger.getLogger(AppleReporterHelper.class.getName());

	public static final String						VERSION													= "1.0";
	public static final String						COMMAND_GET_VENDORS							= "Sales.getVendors";
	public static final String						COMMAND_GET_ACCOUNTS						= "Sales.getAccounts";
	public static final String						COMMAND_GET_REPORT							= "Sales.getReport";
	public static final String						COMMAND_GET_VENDORS_AND_REGIONS	= "Finance.getVendorsAndRegions";

	public enum DateType {
		/**
		 * Daily reports use YYYYMMDD (for example, 20150201)
		 */
		DAILY("Daily"),
		/**
		 * Weekly reports use YYYYMMDD, where the day used is the Sunday that week ends (for example, 20150208)
		 */
		WEEKLY("Weekly"),
		/**
		 * Monthly reports use YYYMM (for example, 201502)
		 */
		MONTHLY("Monthly"),
		/**
		 * Yearly reports use YYYY (for example, 2015)
		 */
		YEARLY("Yearly");

		private final String dateType;

		private DateType(final String dateType) {
			this.dateType = dateType;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return dateType;
		}
	}

	public static byte[] getReport(String userId, String password, String vendor, DateType dateType, Date date) throws IOException, AppleReporterException {
		return getReport(userId, password, null, vendor, dateType, date);
	}

	/**
	 * In case Apple ID has access to multiple accounts, specify the account number
	 *
	 * @param accountName
	 * @param password
	 * @param accountNumber
	 * @return
	 * @throws AppleReporterException
	 */
	public static byte[] getReport(String userId, String password, String accountNumber, String vendor, DateType dateType, Date date) throws IOException, AppleReporterException { // TODO
		date = ApiHelper.removeTime(date);
		boolean downloaded = false;
		Properties clientProperties = new Properties();
		clientProperties.setProperty(ITUNES_USERID_KEY, userId);
		clientProperties.setProperty(ITUNES_PASSWORD_KEY, password);
		if (accountNumber != null) {
			clientProperties.setProperty(ITUNES_ACCOUNT_KEY, accountNumber);
		}
		DateFormat df;
		switch (dateType) {
			case DAILY:
				df = new SimpleDateFormat("yyyyMMdd");
				break;
			case WEEKLY: // For weekly reports the date must be a Sunday
				df = new SimpleDateFormat("yyyyMMdd");
				break;
			case MONTHLY:
				df = new SimpleDateFormat("yyyyMM");
				break;
			case YEARLY:
			default:
				df = new SimpleDateFormat("yyyy");
				break;
		}
		String dateFormatted = df.format(date);

		String[] options = new String[] { vendor + ",", "Sales,", "Summary,", dateType + ",", dateFormatted };

		HttpURLConnection con = executeCommand(COMMAND_GET_REPORT, options, clientProperties);
		if (con.getResponseCode() == 200) {
			String fileName = con.getHeaderField("filename");
			if (null != fileName) {
				byte[] fileContent = IOUtils.toByteArray(con.getInputStream());

				String downloadMessage = con.getHeaderField("downloadMsg");
				LOG.info("iTunes Reporter download message:\n" + downloadMessage);

				return fileContent;
			} else {
				if ((null == con.getHeaderField("EXITCODE")) && (null == con.getHeaderField("ERRORMSG"))) // Apple did not respond with a HTTP 200 ok code but didn't return an exitcode or errormsg either.
																																																	 // Log
					// the error stream
					throw new AppleReporterException(con.getResponseCode(), "iTunes returned error message:\n" + IOUtils.toString(con.getErrorStream()));
				else if (null == con.getHeaderField("ERRORMSG")) throw new AppleReporterException(con.getResponseCode(), "iTunes returned an error message:\n" + IOUtils.toString(con.getErrorStream()));
				else {
					Integer exitCode = Integer.valueOf(con.getHeaderField("EXITCODE"));
					throw new AppleReporterException(exitCode, "iTunes returned an error message:\n" + IOUtils.toString(con.getErrorStream()));
				}
			}

		}

		// try (BufferedReader in = executeCommand(COMMAND_GET_REPORT, options, clientProperties); ByteArrayOutputStream out = new ByteArrayOutputStream(4096);) {
		// if (in == null) return null; // there must have been a problem executing the command / connecting to Itunes
		//
		// String inputLine;
		// while ((inputLine = in.readLine()) != null) {
		//
		// }
		// } catch (Exception ex) {
		// LOG.log(Level.WARNING, "Exception occured while trying to read the report from iTunes", ex);
		// }

		return null;
	}

	public static List<String> getVendors(String userId, String password) throws AppleReporterException, InputValidationException {
		return getVendors(userId, password, null);
	}

	/**
	 * In case Apple ID has access to multiple accounts, specify the account number
	 *
	 * @param accountName
	 * @param password
	 * @param accountNumber
	 * @return
	 */
	public static List<String> getVendors(String userId, String password, String accountNumber) throws AppleReporterException, InputValidationException {
		List<String> vendors = new ArrayList<String>();
		Properties clientProperties = new Properties();
		clientProperties.setProperty(ITUNES_USERID_KEY, userId);
		clientProperties.setProperty(ITUNES_PASSWORD_KEY, password);
		if (accountNumber != null) {
			clientProperties.setProperty(ITUNES_ACCOUNT_KEY, accountNumber);
		}

		List<String> lines = executeCommandAndGetResponceTextAsLines(COMMAND_GET_VENDORS, null, clientProperties);
		Iterator<String> lineIterator = lines.iterator();

		while (lineIterator.hasNext()) {
			String inputLine = lineIterator.next();
			// Parsing depends by the choose Mode (this is Robot.xml mode)
			if (inputLine.contains("<Error>")) {
				String errorCode = lineIterator.next();
				if (errorCode != null && errorCode.contains("<Code>") && errorCode.contains("</Code>")) {
					errorCode = errorCode.trim().replace("<Code>", "").replace("</Code>", "");
					String errorMessage = lineIterator.next();
					if (errorMessage != null && errorMessage.contains("<Message>") && errorMessage.contains("</Message>")) {
						errorMessage = errorMessage.trim().replace("<Message>", "").replace("</Message>", "");
					}
					throw new InputValidationException(Integer.parseInt(errorCode), errorMessage);
				}
			} else if (inputLine.contains("<Vendor>") && inputLine.contains("</Vendor>")) {
				vendors.add(inputLine.trim().replace("<Vendor>", "").replace("</Vendor>", ""));
			}
		}

		return vendors;
	}

	/**
	 * Return a Map Name = Number
	 *
	 * @param accountName
	 * @param password
	 * @return
	 */
	public static Map<String, String> getAccounts(String accountName, String password) throws AppleReporterException {
		HashMap<String, String> accounts = new HashMap<String, String>();
		Properties clientProperties = new Properties();
		clientProperties.setProperty(ITUNES_USERID_KEY, accountName);
		clientProperties.setProperty(ITUNES_PASSWORD_KEY, password);

		List<String> lines = executeCommandAndGetResponceTextAsLines(COMMAND_GET_ACCOUNTS, null, clientProperties);

		Iterator<String> lineIterator = lines.iterator();

		while (lineIterator.hasNext()) {
			String inputLine = lineIterator.next();
			// Parsing depends by the choose Mode (this is Robot.xml mode)
			if (inputLine.contains("<Name>") && inputLine.contains("</Name>")) {
				String name = inputLine.trim().replace("<Name>", "").replace("</Name>", "");

				inputLine = lineIterator.next();
				if (inputLine != null && inputLine.contains("<Number>") && inputLine.contains("</Number>")) {
					String number = inputLine.trim().replace("<Number>", "").replace("</Number>", "");
					accounts.put(name, number);
				}
			}
		}

		return accounts;

	}

	public static List<String> getVendorsAndRegions(String userId, String password) throws AppleReporterException {
		return getVendorsAndRegions(userId, password, null);
	}

	/**
	 * In case Apple ID has access to multiple accounts, specify the account number
	 *
	 * @param accountName
	 * @param password
	 * @param accountNumber
	 * @return
	 */
	public static List<String> getVendorsAndRegions(String userId, String password, String accountNumber) throws AppleReporterException { // TODO
		Properties clientProperties = new Properties();
		clientProperties.setProperty(ITUNES_USERID_KEY, userId);
		clientProperties.setProperty(ITUNES_PASSWORD_KEY, password);
		if (accountNumber != null) {
			clientProperties.setProperty(ITUNES_ACCOUNT_KEY, accountNumber);
		}

		List<String> vendorsAndRegions = executeCommandAndGetResponceTextAsLines(COMMAND_GET_VENDORS_AND_REGIONS, null, clientProperties);

		return vendorsAndRegions;
	}

	private static List<String> executeCommandAndGetResponceTextAsLines(String command, String[] options, Properties clientProperties) throws AppleReporterException {
		HttpURLConnection con = null;

		try {
			con = executeCommand(command, options, clientProperties);

			int responseCode = con.getResponseCode();
			if (responseCode == 200) return IOUtils.readLines(con.getInputStream());

			if ((null == con.getHeaderField("EXITCODE")) && (null == con.getHeaderField("ERRORMSG"))) // Apple did not respond with a HTTP 200 ok code but didn't return an exitcode or errormsg either. Log
																																																 // the error stream
				throw new AppleReporterException(con.getResponseCode(), "iTunes returned error message:\n" + IOUtils.toString(con.getErrorStream()));
			else if (null == con.getHeaderField("ERRORMSG")) throw new AppleReporterException(con.getResponseCode(), "iTunes returned an error message:\n" + IOUtils.toString(con.getErrorStream()));
			else {
				Integer exitCode = Integer.valueOf(con.getHeaderField("EXITCODE"));
				throw new AppleReporterException(exitCode, "iTunes returned an error message:\n" + IOUtils.toString(con.getErrorStream()));
			}
		} catch (IOException iox) {
			throw new AppleReporterException(-1, iox);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private static HttpURLConnection executeCommand(String command, String[] options, Properties clientProperties) throws AppleReporterException {
		HttpURLConnection con = null;
		BufferedReader in = null;

		addCommonProperties(clientProperties);

		con = getConnection(clientProperties, command.substring(0, command.indexOf(".")), "xml");
		executeCommandOnConnection(command, options, clientProperties, con);

		return con;
	}

	private static void executeCommandOnConnection(String command, String[] options, Properties clientProperties, HttpURLConnection con) throws AppleReporterException {
		try {
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setDoOutput(true);

			String[] args;
			if (options != null) {
				args = new String[2 + options.length];
				args[0] = "p=Reporter.properties";
				args[1] = command;
				for (int i = 0; i < options.length; i++) {
					args[i + 2] = options[i];
				}
			} else {
				args = new String[] { "p=Reporter.properties", command };
			}

			String queryString = buildQueryString(args, clientProperties);

			OutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(queryString.getBytes());
			wr.flush();
			wr.close();
		} catch (IOException io) {
			throw new AppleReporterException(-1, io);
		}
	}

	private static void addCommonProperties(Properties clientProperties) {
		clientProperties.setProperty(ITUNES_SALESURL_KEY, ITUNES_SALESSERVICE_URL);
		clientProperties.setProperty(ITUNES_FINANCEURL_KEY, ITUNES_FINANCESERVICE_URL);
		clientProperties.setProperty(ITUNES_MODE_KEY, ITUNES_MODE); // Normal or Robot.xml
	}

	private static String buildQueryString(String[] input, Properties clientProperties) throws IOException {
		StringBuilder inputString = new StringBuilder();
		String createInput = Arrays.toString(input);
		createInput = createInput.replaceAll(",\\s,,\\s", ",");
		createInput = createInput.replaceAll(",,\\s", ",");
		String commandArgsArray = URLEncoder.encode(createInput, "UTF-8");

		try {
			JSONObject jsonObject = new JSONObject();
			Enumeration<Object> e = clientProperties.keys();
			while (e.hasMoreElements()) {
				String key = URLEncoder.encode(e.nextElement().toString(), "UTF-8");
				String value = clientProperties.getProperty(key) != null ? URLEncoder.encode(clientProperties.getProperty(key), "UTF-8") : "";
				jsonObject.put(key.toLowerCase(), value);
			}
			jsonObject.put("version", VERSION);
			jsonObject.put("queryInput", commandArgsArray);
			inputString.append("jsonRequest");
			inputString.append("=");
			inputString.append(jsonObject.toString());
		} catch (JSONException e1) {
			throw new IOException();
		}

		return inputString.toString();
	}

	public static HttpURLConnection getConnection(Properties clientProperties, String appName, String outputStyle) throws AppleReporterException {
		URL url = null;
		HttpURLConnection uc = null;
		try {
			url = getWebserviceURLConnection(clientProperties, appName, outputStyle);
			uc = (HttpURLConnection) url.openConnection();
		} catch (Exception ex) {
			throw new AppleReporterException(-1, ex);
		}
		return uc;
	}

	private static URL getWebserviceURLConnection(Properties clientProperties, String appName, String outputStyle) throws MalformedURLException {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> propMap = new HashMap(clientProperties);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> propUpdatedMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		propUpdatedMap.putAll(propMap);

		String webserviceURL = propUpdatedMap.get(appName + "Url");

		URL obj = new URL(webserviceURL);
		return obj;
	}

	@SuppressWarnings("serial")
	public static class AppleReporterException extends Exception {
		private final int errorCode;

		public AppleReporterException(int errorCode, String errorMessage) {
			super(errorMessage);
			this.errorCode = errorCode;
		}

		public AppleReporterException(int errorCode, Throwable ex) {
			super(ex);
			this.errorCode = errorCode;
		}

		public int getErrorCode() {
			return errorCode;
		}
	}
}
