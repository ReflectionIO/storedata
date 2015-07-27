//
//  GoogleCloudClientHelper.java
//  storedata
//
//  Created by mamin on 20 Jul 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.AbstractMap.SimpleEntry;

/**
 * @author mamin
 *
 */
public class GoogleCloudClientHelper {

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
}
