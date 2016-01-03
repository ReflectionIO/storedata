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
import java.io.InputStreamReader;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.willshex.gson.json.service.server.InputValidationException;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AppleReporterHelper {

	public static final String VERSION = "1.0";
	public static final String COMMAND_GET_VENDORS = "Sales.getVendors";
	public static final String COMMAND_GET_ACCOUNTS = "Sales.getAccounts";
	public static final String COMMAND_GET_REPORT = "Sales.getReport";
	public static final String COMMAND_GET_VENDORS_AND_REGIONS = "Finance.getVendorsAndRegions";

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

	public static boolean getReport(String userId, String password, String vendor, DateType dateType, Date date) throws IOException {
		return getReport(userId, password, null, vendor, dateType, date);
	}

	/**
	 * In case Apple ID has access to multiple accounts, specify the account number
	 * 
	 * @param accountName
	 * @param password
	 * @param accountNumber
	 * @return
	 */
	public static boolean getReport(String userId, String password, String accountNumber, String vendor, DateType dateType, Date date) throws IOException { // TODO
		date = ApiHelper.removeTime(date);
		boolean downloaded = false;
		Properties clientProperties = new Properties();
		clientProperties.setProperty("UserId", userId);
		clientProperties.setProperty("Password", password);
		if (accountNumber != null) {
			clientProperties.setProperty("Account", accountNumber);
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

		BufferedReader in = executeCommand(COMMAND_GET_REPORT, options, clientProperties);
		// String inputLine;
		// while ((inputLine = in.readLine()) != null) {
		// // Parsing depends by the choose Mode (this is Robot.xml mode)
		//
		// }
		in.close();

		return downloaded;
	}

	public static List<String> getVendors(String userId, String password) throws IOException, InputValidationException {
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
	public static List<String> getVendors(String userId, String password, String accountNumber) throws IOException, InputValidationException {
		List<String> vendors = new ArrayList<String>();
		Properties clientProperties = new Properties();
		clientProperties.setProperty("UserId", userId);
		clientProperties.setProperty("Password", password);
		if (accountNumber != null) {
			clientProperties.setProperty("Account", accountNumber);
		}

		BufferedReader in = executeCommand(COMMAND_GET_VENDORS, null, clientProperties);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			// Parsing depends by the choose Mode (this is Robot.xml mode)
			if (inputLine.contains("<Error>")) {
				String errorCode = in.readLine();
				if (errorCode != null && errorCode.contains("<Code>") && errorCode.contains("</Code>")) {
					errorCode = errorCode.trim().replace("<Code>", "").replace("</Code>", "");
					String errorMessage = in.readLine();
					if (errorMessage != null && errorMessage.contains("<Message>") && errorMessage.contains("</Message>")) {
						errorMessage = errorMessage.trim().replace("<Message>", "").replace("</Message>", "");
					}
					in.close();
					throw new InputValidationException(Integer.parseInt(errorCode), errorMessage);
				}
			} else if (inputLine.contains("<Vendor>") && inputLine.contains("</Vendor>")) {
				vendors.add(inputLine.trim().replace("<Vendor>", "").replace("</Vendor>", ""));
			}
		}
		in.close();

		return vendors;
	}

	/**
	 * Return a Map Name = Number
	 * 
	 * @param accountName
	 * @param password
	 * @return
	 */
	public static Map<String, String> getAccounts(String accountName, String password) throws IOException {
		HashMap<String, String> accounts = new HashMap<String, String>();
		Properties clientProperties = new Properties();
		clientProperties.setProperty("UserId", accountName);
		clientProperties.setProperty("Password", password);

		BufferedReader in = executeCommand(COMMAND_GET_ACCOUNTS, null, clientProperties);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			// Parsing depends by the choose Mode (this is Robot.xml mode)
			if (inputLine.contains("<Name>") && inputLine.contains("</Name>")) {
				String name = inputLine.trim().replace("<Name>", "").replace("</Name>", "");
				inputLine = in.readLine();
				if (inputLine != null && inputLine.contains("<Number>") && inputLine.contains("</Number>")) {
					String number = inputLine.trim().replace("<Number>", "").replace("</Number>", "");
					accounts.put(name, number);
				}
			}
		}
		in.close();

		return accounts;
	}

	public static List<String> getVendorsAndRegions(String userId, String password) throws IOException {
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
	public static List<String> getVendorsAndRegions(String userId, String password, String accountNumber) throws IOException { // TODO
		List<String> vendorsAndRegions = new ArrayList<String>();
		Properties clientProperties = new Properties();
		clientProperties.setProperty("UserId", userId);
		clientProperties.setProperty("Password", password);
		if (accountNumber != null) {
			clientProperties.setProperty("Account", accountNumber);
		}

		BufferedReader in = executeCommand(COMMAND_GET_VENDORS_AND_REGIONS, null, clientProperties);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			// Parsing depends by the choose Mode (this is Robot.xml mode)
			// if (inputLine.contains("<Vendor>") && inputLine.contains("</Vendor>")) {
			// vendors.add(inputLine.trim().replace("<Vendor>", "").replace("</Vendor>", ""));
			// }
			vendorsAndRegions.add(inputLine); // TODO use XML parser
		}
		in.close();

		return vendorsAndRegions;
	}

	private static BufferedReader executeCommand(String command, String[] options, Properties clientProperties) throws IOException {
		HttpURLConnection con = null;
		BufferedReader in = null;

		clientProperties.setProperty("SalesUrl", "https://reportingitc-reporter.apple.com/reportservice/sales/v1");
		clientProperties.setProperty("FinanceUrl", "https://reportingitc-reporter.apple.com/reportservice/finance/v1");
		clientProperties.setProperty("Mode", "Robot.xml"); // Normal or Robot.xml

		try {

			con = getConnection(clientProperties, command.substring(0, command.indexOf(".")), "xml");
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
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		} finally {
			if (con != null) {
				con.disconnect();
				con = null;
			}
		}

		return in;
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

	public static HttpURLConnection getConnection(Properties clientProperties, String appName, String outputStyle) throws MalformedURLException {
		URL url = null;
		HttpURLConnection uc = null;
		try {
			url = getWebserviceURLConnection(clientProperties, appName, outputStyle);
			uc = (HttpURLConnection) url.openConnection();
		} catch (Exception ex) {

		}
		return uc;
	}

	private static URL getWebserviceURLConnection(Properties clientProperties, String appName, String outputStyle) throws MalformedURLException {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> propMap = new HashMap(clientProperties);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> propUpdatedMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		propUpdatedMap.putAll(propMap);

		String webserviceURL = (String) propUpdatedMap.get(appName + "Url");

		URL obj = new URL(webserviceURL);
		return obj;
	}

}
