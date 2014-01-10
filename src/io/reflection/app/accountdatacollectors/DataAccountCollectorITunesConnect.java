//
//  DataAccountCollectorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.logging.GaeLevel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.gson.json.shared.Convert;

/**
 * @author billy1380
 * 
 */
public class DataAccountCollectorITunesConnect implements DataAccountCollector {

	public static final String ACCOUNT_DATA_BUCKET_KEY = "account.data.bucket";

	private static final Logger LOG = Logger.getLogger(DataAccountCollectorITunesConnect.class.getName());

	private static final String USERNAME_KEY = "USERNAME";
	private static final String PASSWORD_KEY = "PASSWORD";
	private static final String VENDOR_NUMBER_KEY = "VNDNUMBER";

	private static final String TYPE_KEY = "TYPEOFREPORT";

	private static final String DATE_TYPE_KEY = "DATETYPE";

	private static final String REPORT_TYPE_KEY = "REPORTTYPE";
	private static final String REPORT_DATE_KEY = "REPORTDATE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#validateProperties(java.lang.String)
	 */
	@Override
	public void validateProperties(String properties) throws ServiceException {
		try {
			JsonObject jsonProperties = Convert.toJsonObject(properties);

			JsonElement element = jsonProperties.get("vendors");

			if (element == null) throw new InputValidationException(-1, "");

			JsonArray vendors = element.getAsJsonArray();

			if (vendors == null) throw new InputValidationException(-1, "");

			for (int i = 0; i < vendors.size(); i++) {
				element = vendors.get(i);

				if (element != null) {
					String vendor = element.getAsString();

					if (vendor == null) throw new InputValidationException(-1, "");

					// e.g. 80012345
					if (!vendor.matches("8[0-9]{7}")) throw new InputValidationException(-1, "");
				}
			}

		} catch (JsonParseException pe) {
			if (LOG.isLoggable(GaeLevel.WARNING)) {
				LOG.log(GaeLevel.WARNING, String.format("Error parsing properties [%s] properties", properties), pe);
			}

			throw new InputValidationException(-1, "");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#collect(io.reflection.app.shared.datatypes.DataAccount, java.util.Date)
	 */
	@Override
	public void collect(DataAccount dataAccount, Date date) {
		String dateParameter = (new SimpleDateFormat("yyyyMMdd")).format(date);

		if (LOG.isLoggable(GaeLevel.INFO)) {
			LOG.info(String.format("Getting data from itunes connect for data account [%d] and date [%s]", dataAccount.id.longValue(), dateParameter));
		}

		URL url;
		try {
			url = new URL("https://reportingitc.apple.com/autoingestion.tft?");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

			String vendorId = getVendorId(dataAccount.properties);

			if (vendorId != null) {
				String data = USERNAME_KEY + "=" + dataAccount.username;
				data += "&" + PASSWORD_KEY + "=" + dataAccount.password;
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
					if (LOG.isLoggable(GaeLevel.WARNING)) {
						LOG.warning(String.format("itunes connect return error message [%s] while trying to obtain data with request [%s] ", error, data));
					}
				} else if (connection.getHeaderField("filename") != null) {
					getFile(dataAccount, connection);
				}
			}

		} catch (Exception e) {
			LOG.log(GaeLevel.SEVERE,
					String.format("Exception throw while obtaining file for data account [%d] and date [%s]", dataAccount.id.longValue(), dateParameter), e);
		}

	}

	/**
	 * Gets the first vendor id in a properties string containing a json object
	 * 
	 * @param properties
	 * @return
	 */
	private String getVendorId(String properties) {
		String vendorId = null;

		JsonObject jsonObject = Convert.toJsonObject(properties);

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

	private static void getFile(DataAccount account, HttpURLConnection paramHttpURLConnection) throws IOException {
		String str = paramHttpURLConnection.getHeaderField("filename");
		int i = 0;
		boolean downloaded = false;
		BufferedInputStream localBufferedInputStream = null;
		BufferedOutputStream localBufferedOutputStream = null;
		GcsOutputChannel writeChannel = null;

		try {
			localBufferedInputStream = new BufferedInputStream(paramHttpURLConnection.getInputStream());

			GcsService fileService = GcsServiceFactory.createGcsService();
			GcsFilename fileName = new GcsFilename(System.getProperty(ACCOUNT_DATA_BUCKET_KEY), account.id.toString() + "/" + str);

			writeChannel = fileService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
			byte[] byteBuffer = new byte[1024];

			while ((i = localBufferedInputStream.read(byteBuffer)) != -1) {
				writeChannel.write(ByteBuffer.wrap(byteBuffer, 0, i));
			}

			downloaded = true;
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

		if (downloaded) {
			if (LOG.isLoggable(GaeLevel.INFO)) {
				LOG.warning("File Downloaded Successfully");
			}

			// TODO: store a refernce to the files somewhere
		}
	}
}
