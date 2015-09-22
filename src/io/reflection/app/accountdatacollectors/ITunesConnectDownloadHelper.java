//
//  ITunesConnectDownloadHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import io.reflection.app.logging.GaeLevel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.willshex.gson.json.shared.Convert;

/**
 * @author William Shakour (billy1380)
 *
 */
public class ITunesConnectDownloadHelper {

	private static final Logger LOG = Logger.getLogger(ITunesConnectDownloadHelper.class.getName());

	private static final String USERNAME_KEY = "USERNAME";
	private static final String PASSWORD_KEY = "PASSWORD";
	private static final String VENDOR_NUMBER_KEY = "VNDNUMBER";

	private static final String TYPE_KEY = "TYPEOFREPORT";

	private static final String DATE_TYPE_KEY = "DATETYPE";

	private static final String REPORT_TYPE_KEY = "REPORTTYPE";
	private static final String REPORT_DATE_KEY = "REPORTDATE";

	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");

	public static String getITunesSalesFile(String username, String password, String vendorId, String dateParameter, String bucketName, String bucketPath)
			throws Exception {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Getting Itunes Sales file for username: %s and vendorId%s for dateParam: %s to store in bucket/path: %s/%s",
					username, vendorId, dateParameter, bucketName, bucketPath));
		}

		if (vendorId == null) return null;

		try {
			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, "Getting post data");
			}
			final String data = getPostData(username, password, vendorId, dateParameter);

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, "Connecting to Itunes");
			}
			final HttpURLConnection connection = connectToItunesConnect(data);

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, "Checking error message header: ");
			}
			String error = null;
			if ((error = connection.getHeaderField("ERRORMSG")) != null) {
				// OK error
				// Daily reports are available only for past 30 days, please enter a date within past 30 days.

				if (LOG.isLoggable(Level.WARNING)) {
					if (data != null && password != null) {
						// remove the password for the purposes of logging
						data.replace(password, "**********");
					}

					LOG.warning(String.format("itunes connect return error message [%s] while trying to obtain data with request [%s] ", error, data));
				}

				throw new Exception(error);
			}

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("The error message header is empty. Connection response code: %d", connection.getResponseCode()));


				final Map<String, List<String>> headerFields = connection.getHeaderFields();
				for (final String headerKey : headerFields.keySet()) {
					LOG.log(GaeLevel.DEBUG, String.format("Connection Header %s = %s", headerKey, connection.getHeaderField(headerKey)));
				}
			}

			if (connection != null && connection.getHeaderField("filename") != null) {
				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Filename in connection is %s", connection.getHeaderField("filename")));
				}

				if (bucketName == null || bucketPath == null) return null;

				return getFile(bucketName, bucketPath, connection);
			}
		} catch (final IOException e) {
			final String message = String.format("Exception throw while obtaining file for data account [%s] and date [%s]", username, dateParameter);

			LOG.log(Level.SEVERE, message, e);

			throw new Exception(message);
		}

		return null;
	}

	/**
	 * @param username
	 * @param password
	 * @param vendorId
	 * @param dateParameter
	 * @param bucketName
	 * @param bucketPath
	 * @param fileName
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ProtocolException
	 * @throws Exception
	 */
	public static HttpURLConnection connectToItunesConnect(String data)
			throws IOException, ProtocolException, Exception {
		HttpURLConnection connection = null;

		final URL url = new URL("https://reportingitc.apple.com/autoingestion.tft?");

		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setDoOutput(true);

		final OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(connection.getOutputStream());


		localOutputStreamWriter.write(data);
		localOutputStreamWriter.flush();
		localOutputStreamWriter.close();

		return connection;
	}

	/**
	 * @param username
	 * @param password
	 * @param vendorId
	 * @param dateParameter
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getPostData(String username, String password, String vendorId, String dateParameter) throws UnsupportedEncodingException {
		String data = USERNAME_KEY + "=" + URLEncoder.encode(username, "UTF-8");
		data += "&" + PASSWORD_KEY + "=" + URLEncoder.encode(password, "UTF-8");
		data += "&" + VENDOR_NUMBER_KEY + "=" + vendorId;

		data = data + "&" + TYPE_KEY + "=Sales";

		data = data + "&" + DATE_TYPE_KEY + "=Daily";
		data = data + "&" + REPORT_TYPE_KEY + "=Summary";

		data = data + "&" + REPORT_DATE_KEY + "=" + dateParameter;
		return data;
	}

	private static String getFile(String bucketName, String bucketPath, HttpURLConnection paramHttpURLConnection) throws IOException {
		final String str = paramHttpURLConnection.getHeaderField("filename");
		int i = 0;

		BufferedInputStream localBufferedInputStream = null;
		final BufferedOutputStream localBufferedOutputStream = null;
		GcsOutputChannel writeChannel = null;

		String cloudStorageFileName;

		try {
			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, "Getting input stream from Itunes connection");
			}
			localBufferedInputStream = new BufferedInputStream(paramHttpURLConnection.getInputStream());

			final GcsService fileService = GcsServiceFactory.createGcsService();
			final GcsFilename fileName = new GcsFilename(bucketName, bucketPath + "/" + str);

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Writing to bucket: %s and path %s", bucketName, bucketPath + "/" + str));
			}

			writeChannel = fileService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
			final byte[] byteBuffer = new byte[1024];

			while ((i = localBufferedInputStream.read(byteBuffer)) != -1) {
				writeChannel.write(ByteBuffer.wrap(byteBuffer, 0, i));
			}

			if (LOG.isLoggable(Level.INFO)) {
				LOG.warning("File Downloaded Successfully");
			}

			cloudStorageFileName = "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName();

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("cloudStorageFileName = %s", cloudStorageFileName));
			}
		} finally {
			if (localBufferedInputStream != null) {
				localBufferedInputStream.close();
			}

			if (localBufferedOutputStream != null) {
				localBufferedOutputStream.close();
			}

			if (writeChannel != null) {
				writeChannel.close();
			}
		}

		return cloudStorageFileName;
	}

	/**
	 * Gets the first vendor id in a properties string containing a json object
	 *
	 * @param jsonProperties
	 * @return
	 */
	public static String getVendorId(String jsonProperties) {
		String vendorId = null;

		final JsonObject jsonObject = Convert.toJsonObject(jsonProperties);

		JsonElement element = jsonObject.get("vendors");

		if (element != null) {
			final JsonArray vendors = element.getAsJsonArray();

			if (vendors != null && vendors.size() > 0) {
				element = vendors.get(0);

				if (element != null) {
					vendorId = element.getAsString();
				}
			} else {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.warning("Vendors array is either null or empty");
				}
			}

		} else {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.warning("Vendors array could not be found in properties");
			}
		}

		return vendorId;
	}
}
