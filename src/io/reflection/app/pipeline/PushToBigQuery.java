//
//  PushToBigQuery.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableRow;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author William Shakour (billy1380)
 *
 */
public class PushToBigQuery extends Job2<Void, String, Long> {

	private static final long serialVersionUID = 1L;

	private static final String OAUTH_P12_PROPERTY_KEY = "oauth.p12.file";
	private static final String OAUTH_ACC_ID_PROPERTY_KEY = "oauth.account.id";
	
	private static final String SCOPE = "https://www.googleapis.com/auth/bigquery";
	private static final List<String> SCOPES = Arrays.asList(new String[] { SCOPE });
	private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final Bigquery bigquery = new Bigquery(HTTP_TRANSPORT, JSON_FACTORY, getRequestInitializer());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Void> run(String slimmedRanks, Long feedFetchId) throws Exception {
		FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetchId);

		if ((slimmedRanks != null && !slimmedRanks.equals("null")) && feedFetch != null) {
			JsonArray array = (new JsonParser()).parse(slimmedRanks).getAsJsonArray();

			final int size = array.size();
			if (size > 0) {
				TableDataInsertAllRequest.Rows rows = new TableDataInsertAllRequest.Rows();
				rows.setInsertId(feedFetchId.toString());

				Item item;
				TableRow row;
				JsonObject object;

				for (int i = 0; i < size; i++) {
					object = array.get(i).getAsJsonObject();

					item = new Item();
					item.fromJson(object);

					row = new TableRow();
					row.set("feedfetchid", feedFetch.id);
					row.set("categoryid", feedFetch.category.id);
					row.set("code", feedFetch.code);
					row.set("country", feedFetch.country);
					row.set("store", feedFetch.store);
					row.set("listtype", feedFetch.type);
					row.set("date", feedFetch.date);
					row.set("currency", item.currency);
					row.set("itemid", item.internalId);
					row.set("position", Integer.valueOf(i + 1));
					row.set("price", item.price);

					rows.setJson(row);
				}

				List<TableDataInsertAllRequest.Rows> rowList = new ArrayList<>();
				rowList.add(rows);

				TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows(rowList);
				// TableDataInsertAllResponse response =
				bigquery.tabledata().insertAll(getProjectId(), "pipelinetest", "rank", content).execute();
			}
		}

		return null;
	}

	// /*
	// * Queries BigQuery without polling or paging. Use this function for small, quick queries that return less than 100 rows and take less than 5 seconds to
	// * process.
	// */
	// private QueryResponse queryBigqueryQuick(String query) throws IOException {
	// return bigquery.jobs().query(getProjectId(), new QueryRequest().setQuery(query)).execute();
	// }

	private String getProjectId() {
		// In App Engine each app has a BigQuery project id with the same name as the app.
		// You'll need to turn on the BigQuery API on the cloud console for this to work.
		return AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName().split("@")[0];
	}

	private static HttpRequestInitializer getRequestInitializer() {
		if (System.getProperty(OAUTH_P12_PROPERTY_KEY) != null) {
			// Good for testing and localhost environments, where no AppIdentity is available.
			return authorize(System.getProperty(OAUTH_P12_PROPERTY_KEY), System.getProperty(OAUTH_ACC_ID_PROPERTY_KEY));
		}

		return new AppIdentityCredential(SCOPES);
	}

	private static Credential authorize(String p12, String accountId) {
		GoogleCredential c = null;
		try {
			URI file = PushToBigQuery.class.getResource(p12).toURI();
			c = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY).setServiceAccountId(accountId)
					.setServiceAccountScopes(SCOPES).setServiceAccountPrivateKeyFromP12File(new File(file)).build();
		} catch (GeneralSecurityException | IOException | URISyntaxException e) {}
		return c;
	}
}
