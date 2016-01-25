//
//  GoogleCloudClientHelper.java
//  storedata
//
//  Created by mamin on 20 Jul 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import io.reflection.app.logging.GaeLevel;

/**
 * @author mamin
 *
 */
public class GoogleCloudClientHelper {
	private transient static final Logger LOG = Logger.getLogger(GoogleCloudClientHelper.class.getName());

	/**
	 * @param data
	 * @return
	 */
	public static SimpleEntry<String, String> getGCSBucketAndFileName(String data) {
		int fileNameStartsAt = data.indexOf('/', "/gs/".length() + 1);

		String bucket = data.substring("/gs/".length(), fileNameStartsAt);
		String fileName = data.substring(fileNameStartsAt + 1);

		return new SimpleEntry<String, String>(bucket, fileName);
	}

	public static String uploadFileToGoogleCloudStorage(String bucketName, String objectName, byte[] content) throws IOException {
		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Uploading to GCS bucket %s with path %s with content length %d", bucketName, objectName, content.length));
		}

		final GcsService fileService = GcsServiceFactory.createGcsService();
		final GcsFilename fileName = new GcsFilename(bucketName, objectName);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Getting the GcsOutputChannel...");
		}
		GcsOutputChannel writeChannel = fileService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "...Writting to GcsOutputChannel...");
		}
		writeChannel.write(ByteBuffer.wrap(content));
		String filePath = "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "...File uploaded with full path: " + filePath);
		}

		return filePath;
	}
}
