//
//  BigQueryHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.bigquery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.DatasetReference;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableRow;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

/**
 * @author William Shakour (billy1380)
 *
 */
public class BigQueryHelper {

	private static final String OAUTH_P12_PROPERTY_KEY = "oauth.p12.file";
	private static final String OAUTH_ACC_ID_PROPERTY_KEY = "oauth.account.id";

	public static final String DATASET_NAME_PROPERTY_KEY = "bigquery.dataset.name";

	private static final String MANAGE_SCOPE = "https://www.googleapis.com/auth/bigquery";
	private static final String INSERT_SCOPE = "https://www.googleapis.com/auth/bigquery.insertdata";
	private static final List<String> SCOPES = Arrays.asList(new String[] { MANAGE_SCOPE, INSERT_SCOPE });
	private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	public static final Bigquery bigquery = new Bigquery(HTTP_TRANSPORT, JSON_FACTORY, getRequestInitializer());

	/**
	 * Queries BigQuery without polling or paging. Use this function for small, quick queries that return less than 100 rows and take less than 5 seconds to
	 * process.
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public static QueryResponse queryBigqueryQuick(String query) throws IOException {
		return bigquery
				.jobs()
				.query(getProjectId(),
						new QueryRequest().setQuery(query)
								.setDefaultDataset(new DatasetReference().setDatasetId(System.getProperty(DATASET_NAME_PROPERTY_KEY)))).execute();
	}

	/**
	 * Queries BigQuery with paging
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public static QueryResponse queryBigqueryPaged(String query, Long start, Long count) throws IOException {
		QueryRequest request = new QueryRequest().setQuery(query).setMaxResults(count)
				.setDefaultDataset(new DatasetReference().setDatasetId(System.getProperty(DATASET_NAME_PROPERTY_KEY)));

		QueryResponse response = bigquery.jobs().query(getProjectId(), request).execute();

		return response;
	}

	/**
	 * Insert all rows (content) into table
	 * 
	 * @param string
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static TableDataInsertAllResponse insertAll(String tableName, TableDataInsertAllRequest content) throws IOException {
		return bigquery.tabledata().insertAll(getProjectId(), System.getProperty(DATASET_NAME_PROPERTY_KEY), tableName, content).execute();
	}

	/**
	 * Gets google project id
	 * 
	 * @return
	 */
	public static String getProjectId() {
		// In App Engine each app has a BigQuery project id with the same name as the app.
		// You'll need to turn on the BigQuery API on the cloud console for this to work.
		return AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName().split("@")[0];
	}

	private static Credential authorize(String p12, String accountId) {
		GoogleCredential c = null;
		try {
			URI file = BigQueryHelper.class.getResource(p12).toURI();
			c = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY).setServiceAccountId(accountId)
					.setServiceAccountScopes(SCOPES).setServiceAccountPrivateKeyFromP12File(new File(file)).build();
		} catch (GeneralSecurityException | IOException | URISyntaxException e) {}
		return c;
	}

	private static HttpRequestInitializer getRequestInitializer() {
		if (System.getProperty(OAUTH_P12_PROPERTY_KEY) != null) {
			// Good for testing and localhost environments, where no AppIdentity is available.
			return authorize(System.getProperty(OAUTH_P12_PROPERTY_KEY), System.getProperty(OAUTH_ACC_ID_PROPERTY_KEY));
		}

		return new AppIdentityCredential(SCOPES);
	}

	/**
	 * 
	 * @param before
	 * @param after
	 * @param dateName
	 * @return
	 */
	public static String beforeAfterQuery(Date before, Date after, String dateName) {
		StringBuffer buffer = new StringBuffer();
		if (before != null && after != null) {
			buffer.append("([" + dateName + "]>");
			buffer.append(after.getTime() * 1000l);
			buffer.append(" AND [" + dateName + "]<");
			buffer.append(before.getTime() * 1000l);
			buffer.append(")");
		} else if (after != null && before == null) {
			buffer.append("[" + dateName + "]>=");
			buffer.append(after.getTime() * 1000l);
		} else if (before != null && after == null) {
			buffer.append("[" + dateName + "]<");
			buffer.append(before.getTime() * 1000l);
		}

		return buffer.toString();
	}

	/**
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static String beforeAfterQuery(Date before, Date after) {
		return beforeAfterQuery(before, after, "date");
	}

	public static Date dateColumn(Object value) {
		return value == null ? null : new Date(Double.valueOf((String) value).longValue() * 1000l);
	}

	public static void tablise(QueryResponse response) {
		if (response != null && response.getRows() != null) {
			for (TableRow row : response.getRows()) {
				for (int i = 0; i < row.getF().size(); i++) {
					row.set(response.getSchema().getFields().get(i).getName(), row.getF().get(i).getV());
				}
			}
		}
	}
}