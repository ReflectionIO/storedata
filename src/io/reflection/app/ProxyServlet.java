package io.reflection.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.util.Base64;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import io.reflection.app.logging.GaeLevel;

@SuppressWarnings("serial")
public class ProxyServlet extends HttpServlet {
	private static final String MEMCACHE_KEY_PREFIX = "ps_";

	private transient static final Logger LOG = Logger.getLogger(ProxyServlet.class.getName());

	private MemcacheService syncCache;
	private AsyncMemcacheService	asyncCache;

	@Override
	public void init() throws ServletException {
		super.init();

		syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));

		asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
		asyncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO add hot linking protection. Make sure the user is coming from our site. This can best be handled by secure cookies.

		String encodedUrlToFetch = req.getParameter("url");

		if (encodedUrlToFetch == null || encodedUrlToFetch.trim().length() == 0) {
			resp.sendError(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
			return;
		}

		String[] cachedObject = (String[]) syncCache.get(MEMCACHE_KEY_PREFIX + encodedUrlToFetch);
		if (cachedObject == null) {
			cachedObject = downloadAndCache(encodedUrlToFetch);
		}

		if (cachedObject == null) {
			resp.sendRedirect(new String(Base64.decodeBase64(encodedUrlToFetch)));
			return;
		}

		resp.setHeader("Cache-Control", "no-transform, max-age=14706830");
		resp.setContentType(cachedObject[0]);
		resp.getOutputStream().write(Base64.decodeBase64(cachedObject[1]));
		resp.getOutputStream().flush();

		return;
	}

	/**
	 * @param encodedUrlToFetch
	 * @return
	 */
	private String[] downloadAndCache(String encodedUrlToFetch) {
		String decodedUrl = new String(Base64.decodeBase64(encodedUrlToFetch));

		HttpURLConnection con = null;
		BufferedInputStream bin = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream(10240); // 10kB should be sufficient for most images

		try {
			URL url = new URL(decodedUrl);

			con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) return null;

			String contentType = con.getContentType();
			bin = new BufferedInputStream(con.getInputStream());

			byte[] buffer = new byte[10240];
			int bytesRead = 0;

			while ((bytesRead = bin.read(buffer)) > 0) {
				bout.write(buffer, 0, bytesRead);
			}

			String encodedContent = Base64.encodeBase64String(bout.toByteArray());

			String[] cachedObject = new String[] { contentType, encodedContent };
			asyncCache.put(MEMCACHE_KEY_PREFIX + encodedUrlToFetch, cachedObject);

			return cachedObject;
		} catch (Exception e) {
			LOG.log(GaeLevel.DEBUG, String.format("Exception occurred while trying to fetched proxied content for url %s. Decoded to: %s", encodedUrlToFetch, decodedUrl), e);
		}finally{
			if (bin != null) {
				try {
					bin.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				con.disconnect();
			}
		}
		return null;
	}
}
