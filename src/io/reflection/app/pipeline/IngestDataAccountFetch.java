//
//  IngestDataAccountFetch.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.accountdataingestors.DataAccountIngestor;
import io.reflection.app.accountdataingestors.DataAccountIngestorFactory;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;

import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

public class IngestDataAccountFetch extends Job1<Long, Long> {

	private static final long serialVersionUID = 3258834001291433964L;

	private static final Logger LOG = Logger.getLogger(IngestDataAccountFetch.class.getName());

	private String name;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Long> run(Long dataAccountFetchId) throws Exception {
		DataAccountFetch fetch = null;
		boolean ingested = false;

		if (dataAccountFetchId != null) {
			try {
				fetch = DataAccountFetchServiceProvider.provide().getDataAccountFetch(dataAccountFetchId);

				if (fetch != null) {
					DataAccount linkedAccount = DataAccountServiceProvider.provide().getDataAccount(fetch.linkedAccount.id);

					if (linkedAccount != null) {
						DataSource source = DataSourceServiceProvider.provide().getDataSource(linkedAccount.source.id);

						fetch.linkedAccount = linkedAccount;

						if (source != null) {
							linkedAccount.source = source;

							DataAccountIngestor ingestor = DataAccountIngestorFactory.getIngestorForSource(source.a3Code);

							if (ingestor != null) {
								ingestor.ingest(fetch);

								ingested = true;
							} else {
								LOG.info(String.format("Could not find ingestor for source with a3Code [%s], skipping", source.a3Code));
							}

						} else {
							LOG.info(String.format("Could not find source for id [%d], skipping", linkedAccount.source.id.longValue()));
						}

					} else {
						LOG.info(String.format("Could not find data account for id [%d], skipping", fetch.linkedAccount.id.longValue()));
					}

				} else {
					LOG.info(String.format("Could not find data account fetch for id [%s], skipping", dataAccountFetchId));
				}

			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Database error occured while trying to ingest data with fetch id [%s]", dataAccountFetchId), e);
			}
		}

		return (fetch == null || !ingested) ? null : immediate(fetch.id);
	}

	public IngestDataAccountFetch name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return name;
	}
}