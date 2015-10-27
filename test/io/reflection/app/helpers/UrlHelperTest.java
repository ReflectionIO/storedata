package io.reflection.app.helpers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.api.client.util.Base64;

public class UrlHelperTest {

	@Test
	public void testWrapUrlInProxy() {
		String url = "http://a1756.phobos.apple.com/us/r1000/056/Purple4/v4/b8/d9/17/b8d917ba-050a-8053-d6eb-49caa99f44bd/mzl.jlilsqnq.53x53-50.png";
		String wrappedUrl = UrlHelper.INSTANCE.wrapUrlInProxy(url);

		assertNotNull("Wrapped url is NULL", wrappedUrl);
		assertTrue("Wrapped url is empty", wrappedUrl.length() > 0);
		assertTrue("Wrapped url does not start with the url to the proxy servlet", wrappedUrl.startsWith("/ps?url="));
		assertTrue("Wrapped url was not formed as expected", wrappedUrl.equals("/ps?url=" + Base64.encodeBase64String(url.getBytes())));
	}
}
