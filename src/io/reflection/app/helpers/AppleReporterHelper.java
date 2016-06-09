//
//  AppleReporterHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 23 Dec 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.willshex.gson.json.service.server.InputValidationException;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AppleReporterHelper {
	/**
	 *
	 */
	private static final int							REPORT_DOWNLOAD_RETRY_COUNT			= 3;
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

	private static final int							APPLE_CONNECTION_READ_TIMEOUT		= 2 * 60 * 1000;																																																																																																																																																																																																																																																																																																																																																																																																																					 // Connection
																																																																																																																																																																																																																																																																																																																																																																																																																																																																	 // timeout
																																																																																																																																																																																																																																																																																																																																																																																																																																																																	 // in
																																																																																																																																																																																																																																																																																																																																																																																																																																																																	 // milliseconds

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
	public static byte[] getReport(String userId, String password, String accountNumber, String vendor, DateType dateType, Date date) throws AppleReporterException {
		date = ApiHelper.removeTime(date);
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

		int retryCount = 0;

		while (retryCount++ < REPORT_DOWNLOAD_RETRY_COUNT) {
			HttpURLConnection con = executeCommand(COMMAND_GET_REPORT, options, clientProperties);

			try {
				if (con.getResponseCode() == 200 && con.getHeaderField("filename") != null) {
					String fileName = con.getHeaderField("filename");
					if (null != fileName) {
						byte[] fileContent = IOUtils.toByteArray(con.getInputStream());

						String downloadMessage = con.getHeaderField("downloadMsg");
						LOG.info("iTunes Reporter download message: " + downloadMessage);

						return fileContent;
					}
				}

				throw getAppleErrorFromResponce(con);
			} catch (IOException e) {
				if (e instanceof SocketTimeoutException) {
					LOG.warning("Socket Timeout Exception on try: " + retryCount + 1);
				} else
					throw new AppleReporterException(-1, e);
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}

		throw new AppleReporterException(-1, "Could not get report after retrying " + REPORT_DOWNLOAD_RETRY_COUNT + " tries.");
	}

	/**
	 * @param con
	 * @return
	 */
	private static AppleReporterException getAppleErrorFromResponce(HttpURLConnection con) {
		int responseCode = -1;
		String errorToLog = null;
		try {
			responseCode = con.getResponseCode();

			List<String> errorLines = IOUtils.readLines(con.getErrorStream());
			Iterator<String> errorMessageLineIterator = errorLines.iterator();

			while (errorMessageLineIterator.hasNext()) {
				String inputLine = errorMessageLineIterator.next();
				// Parsing depends by the choose Mode (this is Robot.xml mode)
				if (inputLine.contains("<Error>")) {
					String errorCode = errorMessageLineIterator.next();
					if (errorCode != null && errorCode.contains("<Code>") && errorCode.contains("</Code>")) {
						errorCode = errorCode.trim().replace("<Code>", "").replace("</Code>", "");
						String errorMessage = errorMessageLineIterator.next();
						if (errorMessage != null && errorMessage.contains("<Message>") && errorMessage.contains("</Message>")) {
							errorMessage = errorMessage.trim().replace("<Message>", "").replace("</Message>", "");
						}
						return new AppleReporterException(Integer.parseInt(errorCode), errorMessage);
					}
				}
			}

			errorToLog = Arrays.toString(errorLines.toArray());
		} catch (IOException io) {
			return new AppleReporterException(-1, io);
		}

		return new AppleReporterException(-1,
				String.format("Unknown Apple Itunes Error. Exit code: %s, HTTP response code: %d, Error Msg: , %sErrorStream:\n%s",
						con.getHeaderField("EXITCODE"),
						responseCode,
						con.getHeaderField("ERRORMSG"),
						errorToLog));
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
			if (inputLine.contains("<Vendor>") && inputLine.contains("</Vendor>")) {
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

	public static String getAccountIdForVendorId(String username, String password, String primaryVendorId) throws AppleReporterException {
		Map<String, String> accounts = AppleReporterHelper.getAccounts(username, password);

		if (accounts == null || accounts.size() == 0) return null;

		for (String name : accounts.keySet()) {
			String accountId = accounts.get(name);

			try {
				List<String> vendors = AppleReporterHelper.getVendors(username, password, accountId);
				if (vendors == null || vendors.size() == 0) {
					continue;
				}

				for (String vendorId : vendors) {
					if (primaryVendorId.equals(vendorId)) return accountId;
				}
			} catch (InputValidationException e) {
				continue;
			}
		}

		return null;
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
	public static List<String> getVendorsAndRegions(String userId, String password, String accountNumber) throws AppleReporterException {
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

			throw getAppleErrorFromResponce(con);
		} catch (IOException iox) {
			throw new AppleReporterException(-1, iox);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private static HttpURLConnection executeCommand(String command, String[] options, Properties clientProperties) throws AppleReporterException {

		addCommonProperties(clientProperties);

		HttpURLConnection con = getConnection(clientProperties, command.substring(0, command.indexOf(".")), "xml");
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
			uc.setReadTimeout(APPLE_CONNECTION_READ_TIMEOUT);
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

	/**
	 * @param date
	 * @return
	 */
	public static boolean isDateBeforeItunesReporterStarted(Date date) {
		return new DateTime(2015, 11, 26, 0, 0).isAfter(new DateTime(date));
	}

	public static int getDaysSinceITunesReportLaunch() {
		int days = Days.daysBetween(new DateTime(2015, 11, 26, 0, 0), new DateTime()).getDays();

		// Apple only provides this data for up to a year
		return days > 365 ? 365 : days;
	}

	@SuppressWarnings("serial")
	public static class AppleReporterException extends Exception {
		private final int errorCode;

		public AppleReporterException(int errorCode, String errorMessage) {
			super(errorCode + ": " + errorMessage);
			this.errorCode = errorCode;
		}

		public AppleReporterException(int errorCode, String errorMessage, Throwable ex) {
			super(errorCode + ": " + errorMessage, ex);
			this.errorCode = errorCode;
		}

		public AppleReporterException(int errorCode, Throwable ex) {
			super("Error code: " + errorCode, ex);
			this.errorCode = errorCode;
		}

		public int getErrorCode() {
			return errorCode;
		}
	}

	public static enum ITunesReporterError {
		CODE_100(100, "Invalid app or command format."),
		CODE_101(101, "Invalid command."),
		CODE_102(102, "Too few or too many parameters specified for the method."),
		CODE_103(103, "No properties file specified."),
		CODE_104(104, "Can’t find properties file."),
		CODE_105(105, "Can’t read properties file."),
		CODE_106(106, "Properties file doesn’t contain an Apple ID or password."),
		CODE_107(107, "The Apple ID or password is in the wrong format."),
		CODE_108(108, "Invalid username and password. Change values and try again."),
		CODE_109(109, "Properties file doesn’t contain an endpoint."),
		CODE_110(110, "Network isn’t available."),
		CODE_111(111, "Network is available but can’t connect to Sales and Trends or Payments and Financial Reports."),
		CODE_112(112, "Can’t save file because there isn’t enough space (or you don’t have write access to the current directory)."),
		CODE_113(113, "Invalid mode. Valid values include: Normal and Robot."),
		CODE_200(200, "Invalid vendor number."),
		CODE_201(201, "Invalid report type."),
		CODE_202(202, "Invalid report subtype."),
		CODE_203(203, "Invalid combination of report type and report subtype."),
		CODE_204(204, "Invalid date type."),
		CODE_205(205, "Invalid weekly date."),
		CODE_206(206, "Invalid combination of report subtype and date type."),
		CODE_207(207, "Invalid date."),
		CODE_208(208, "Invalid combination of date type and date."),
		CODE_209(209, "Report no longer available because it expired."),
		CODE_210(210, "Report not available because it is not ready yet."),
		CODE_211(211, "Report not available."),
		CODE_212(212, "Unexpected error."),
		CODE_213(213, "There were no sales for the date specified."),
		CODE_214(214,
				"You have access to several providers. Specify the provider ID (account number) in your properties file or on the command line. To see a list of providers, use the command getAccounts."),
		CODE_215(215, "Invalid provider ID specified on command line. To see a list of providers, use getAccounts."),
		CODE_216(216, "Invalid provider ID specified in properties file. To see a list of providers, use getAccounts."),
		CODE_217(217, "You do not have access to reports.");

		private final int			errorCode;
		private final String	errorMessage;

		private ITunesReporterError(int errorCode, String errorMessage) {
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
		}

		public int getErrorCode() {
			return errorCode;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public static ITunesReporterError getByCode(int code) {
			try {
				return valueOf("CODE_" + code);
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
	}

	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean areCredentialsValid(String username, String password) {
		try {
			getVendors(username, password);
		} catch (AppleReporterException e) {
			if (e.getErrorCode() == 107 || e.getErrorCode() == 108) return false;
		} catch (InputValidationException e) {
			LOG.log(Level.WARNING, "Exception occured while trying to verify iTunes credentials for username: " + username, e);
		}

		// unless we get an explicit authentication error (107 or 108) we will assume all is well with the credentials.
		// this is to make sure we don't start making data accounts as having invalid credentials if iTunes has an outage
		// or does not return a valid result for any other reason
		return true;
	}

}
