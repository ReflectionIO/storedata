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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spacehopperstudios.utility.StringUtils;
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
		String fileName = null;

		URL url;
		try {
			url = new URL("https://reportingitc.apple.com/autoingestion.tft?");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

			if (vendorId != null) {
				String data = USERNAME_KEY + "=" + StringUtils.urlencode(username);
				data += "&" + PASSWORD_KEY + "=" + StringUtils.urlencode(password);
				data += "&" + VENDOR_NUMBER_KEY + "=" + vendorId;

				data = data + "&" + TYPE_KEY + "=Sales";

				data = data + "&" + DATE_TYPE_KEY + "=Daily";
				data = data + "&" + REPORT_TYPE_KEY + "=Summary";

				data = data + "&" + REPORT_DATE_KEY + "=" + dateParameter;

				localOutputStreamWriter.write(data);
				localOutputStreamWriter.flush();
				localOutputStreamWriter.close();

				String error = null;
				if ((error = connection.getHeaderField("ERRORMSG")) != null) {
					// OK error
					// Daily reports are available only for past 30 days, please enter a date within past 30 days.

					if (LOG.isLoggable(GaeLevel.WARNING)) {
						if (data != null && password != null) {
							// remove the password for the purposes of logging
							data = data.replace("&PASSWORD=" + password, "&PASSWORD=**********");
						}

						LOG.warning(String.format("itunes connect return error message [%s] while trying to obtain data with request [%s] ", error, data));
					}

					throw new Exception(error);
				} else if (connection.getHeaderField("filename") != null) {
					fileName = getFile(bucketName, bucketPath, connection);
				}
			}
		} catch (IOException e) {
			String message = String.format("Exception throw while obtaining file for data account [%s] and date [%s]", username, dateParameter);

			LOG.log(GaeLevel.SEVERE, message, e);

			throw new Exception(message);
		}

		return fileName;
	}

	private static String getFile(String bucketName, String bucketPath, HttpURLConnection paramHttpURLConnection) throws IOException {
		String str = paramHttpURLConnection.getHeaderField("filename");
		int i = 0;

		BufferedInputStream localBufferedInputStream = null;
		BufferedOutputStream localBufferedOutputStream = null;
		GcsOutputChannel writeChannel = null;

		String cloudStorageFileName;

		try {
			localBufferedInputStream = new BufferedInputStream(paramHttpURLConnection.getInputStream());

			GcsService fileService = GcsServiceFactory.createGcsService();
			GcsFilename fileName = new GcsFilename(bucketName, bucketPath + "/" + str);

			writeChannel = fileService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
			byte[] byteBuffer = new byte[1024];

			while ((i = localBufferedInputStream.read(byteBuffer)) != -1) {
				writeChannel.write(ByteBuffer.wrap(byteBuffer, 0, i));
			}

			if (LOG.isLoggable(GaeLevel.INFO)) {
				LOG.warning("File Downloaded Successfully");
			}

			cloudStorageFileName = "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName();

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

		JsonObject jsonObject = Convert.toJsonObject(jsonProperties);

		JsonElement element = jsonObject.get("vendors");

		if (element != null) {
			JsonArray vendors = element.getAsJsonArray();

			if (vendors != null && vendors.size() > 0) {
				element = vendors.get(0);

				if (element != null) {
					vendorId = element.getAsString();
				}
			} else {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.warning("Vendors array is either null or empty");
				}
			}

		} else {
			if (LOG.isLoggable(GaeLevel.INFO)) {
				LOG.warning("Vendors array could not be found in properties");
			}
		}

		return vendorId;
	}
}
