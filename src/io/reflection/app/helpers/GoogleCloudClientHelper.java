//
//  GoogleCloudClientHelper.java
//  storedata
//
//  Created by mamin on 20 Jul 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
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

		int written = writeChannel.write(ByteBuffer.wrap(content));

		writeChannel.close();

		String filePath = "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName();

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "...File uploaded with full path: " + filePath + ", bytes written: " + written);
		}

		return filePath;
	}

	/**
	 * If you specify isGZipped as true, the file content will be returned after being uncompressed
	 *
	 * @param bucketName
	 * @param objectName
	 * @param isGZipped
	 * @return
	 * @throws IOException
	 */
	public static byte[] getFileFromGoogleCloudStorage(String bucketName, String objectName, boolean isGZipped) throws IOException {

		if (bucketName == null || objectName == null) {
			LOG.log(GaeLevel.DEBUG, String.format("BucketName or ObjectName is invalid: %s / %s", bucketName, objectName));
			return null;
		}

		GcsService fileService = GcsServiceFactory.createGcsService();
		GcsFilename cloudFileName = new GcsFilename(bucketName, objectName);

		GcsInputChannel readChannel = null;
		GZIPInputStream gzipInputStream = null;

		try {
			readChannel = fileService.openReadChannel(cloudFileName, 0);
			InputStream gcsInputStream = Channels.newInputStream(readChannel);

			if (isGZipped) {
				gzipInputStream = new GZIPInputStream(gcsInputStream);
			}

			byte[] fileContent = IOUtils.toByteArray(isGZipped ? gzipInputStream : gcsInputStream);
			return fileContent;

		} finally {
			if (gzipInputStream != null) {
				try {
					gzipInputStream.close();
				} catch (IOException e) {
					LOG.log(Level.WARNING, "Exception occured while closing gzip stream", e);
				}
			}

			if (readChannel != null) {
				readChannel.close();
			}
		}
	}
}
