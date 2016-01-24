//
//  UrlHelper.java
//  storedata
//
//  Created by mamin on 27 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import com.google.api.client.util.Base64;

/**
 * @author mamin
 *
 */
public class UrlHelper {
	public static final UrlHelper INSTANCE = new UrlHelper();

	private UrlHelper() {
	}

	public String wrapUrlInProxy(String url) {
		return "/ps?url=" + Base64.encodeBase64String(url.getBytes());
	}
}
