/**
 * 
 */
package com.spacehopperstudios.storedatacollector;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.spacehopperstudios.storedatacollector.collectors.DataStoreDataCollector;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class DevHelperServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(DevHelperServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter("action");
		String object = req.getParameter("object");
		
		boolean success = false;
		
		if (action != null) {
			if ("uningest".toUpperCase().equals(action.toUpperCase())) {
				DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
				
				Query query = new Query("FeedFetch");
				PreparedQuery preparedQuery = datastoreService.prepare(query);
				
				for (Entity entity : preparedQuery.asIterable()) {
					entity.setProperty(DataStoreDataCollector.ENTITY_COLUMN_INGESTED, Boolean.FALSE);
					datastoreService.put(entity);
				}
				
				success = true;
			} else if ("remove".toUpperCase().equals(action.toUpperCase())) {
				if (object != null) {
					DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
					
					Query query = new Query(object);
					PreparedQuery preparedQuery = datastoreService.prepare(query);
					
					for (Entity entity : preparedQuery.asIterable()) {
						datastoreService.delete(entity.getKey());
					}
					
					success = true;
				}
			}
		}
		
		
		
		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Imported completed status = [%s]", success ? "success" : "failure"));
		}

		resp.setHeader("Cache-Control", "no-cache");
		resp.getOutputStream().print(success ? "success" : "failure");
		
	}

}
