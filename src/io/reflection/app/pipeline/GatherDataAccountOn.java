//
//  GatherDataAccountOn.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.accountdatacollectors.DataAccountCollector;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;

import java.util.Date;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

public class GatherDataAccountOn extends Job2<Long, Long, Date> {

	private static final long serialVersionUID = -8706042892487009601L;

	private static final Logger LOG = Logger.getLogger(GatherDataAccountOn.class.getName());

	private transient String name = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Long> run(Long dataAccountId, Date date) throws Exception {
		Value<Long> futureCollectedFetchId = null;

		try {
			DataAccount account = DataAccountServiceProvider.provide().getDataAccount(dataAccountId);

			if (account != null) {
				if (date == null) {
					date = DateTime.now(DateTimeZone.UTC).toDate();
				}

				DataSource dataSource = DataSourceServiceProvider.provide().getDataSource(account.source.id);

				if (dataSource != null) {
					account.source = dataSource;

					DataAccountCollector collector = DataAccountCollectorFactory.getCollectorForSource(dataSource.a3Code);

					if (collector != null) {
						DataAccountFetch dataAccountFetch = collector.collect(account, date);

						if (dataAccountFetch != null && dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
							futureCollectedFetchId = futureCall(new IngestDataAccountFetch().name("Add sales"), immediate(dataAccountFetch.id),
									PipelineSettings.onDataAccountIngestQueue);
						}
					} else {
						if (LOG.isLoggable(GaeLevel.WARNING)) {
							LOG.log(GaeLevel.WARNING, "Could not find a collector for [%s]", dataSource.a3Code);
						}
					}
				} else {
					if (LOG.isLoggable(GaeLevel.WARNING)) {
						LOG.log(GaeLevel.WARNING, "Could not find a data source for id [%d]", account.source.id.longValue());
					}
				}
			}
		} catch (DataAccessException e) {
			LOG.log(GaeLevel.SEVERE,
					String.format("Database error occured while trying to import data with accountid [%s] and date [%s]", dataAccountId, date), e);

			throw e;
		}

		return futureCollectedFetchId;
	}

	public GatherDataAccountOn name(String value) {
		name = value;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return (name == null ? super.getJobDisplayName() : name);
	}

}