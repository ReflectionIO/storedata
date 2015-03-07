//
//  GatherDataAccountOn.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import static io.reflection.app.accountdatacollectors.DataAccountCollector.ACCOUNT_DATA_BUCKET_KEY;
import io.reflection.app.accountdatacollectors.DataAccountCollector;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.accountdatacollectors.ITunesConnectDownloadHelper;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;

import java.util.Date;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

public class GatherDataAccountOn extends Job2<Long, Long, Date> {

	private static final long serialVersionUID = -8706042892487009601L;

	private static final Logger LOG = Logger.getLogger(GatherDataAccountOn.class.getName());

	private String name;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Long> run(Long dataAccountId, Date date) throws Exception {
		boolean sendNotification = false;
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
						futureCollectedFetchId = collectAndIngest(account, date);

						if (futureCollectedFetchId != null && sendNotification) {
							futureCall(new NotifyUser().name("Notify account owner"), futureCollectedFetchId, immediate(dataAccountId), PipelineSettings.onDefaultQueue);
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

	private FutureValue<Long> collectAndIngest(DataAccount dataAccount, Date date) throws DataAccessException {
		date = ApiHelper.removeTime(date);
		FutureValue<Long> ingestedFetchId = null;

		String dateParameter = ITunesConnectDownloadHelper.DATE_FORMATTER.format(date);

		if (LOG.isLoggable(GaeLevel.INFO)) {
			LOG.info(String.format("Getting data from itunes connect for data account [%s] and date [%s]", dataAccount.id == null ? dataAccount.username
					: dataAccount.id.toString(), dateParameter));
		}

		boolean success = false;
		String cloudFileName = null, error = null;
		try {
			cloudFileName = ITunesConnectDownloadHelper.getITunesSalesFile(dataAccount.username, dataAccount.password,
					ITunesConnectDownloadHelper.getVendorId(dataAccount.properties), dateParameter, System.getProperty(ACCOUNT_DATA_BUCKET_KEY),
					dataAccount.id.toString());
		} catch (Exception ex) {
			error = ex.getMessage();
		}

		DataAccountFetch dataAccountFetch = DataAccountFetchServiceProvider.provide().getDateDataAccountFetch(dataAccount, date);

		if (dataAccountFetch == null) {
			dataAccountFetch = new DataAccountFetch();

			dataAccountFetch.date = date;
			dataAccountFetch.linkedAccount = dataAccount;
		}

		if (dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
			if (error != null) {
				if (error.startsWith("There are no reports") || error.startsWith("There is no report")) {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
				} else {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeError;
				}

				dataAccountFetch.data = error;
			} else if (cloudFileName != null) {
				dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered;
				dataAccountFetch.data = cloudFileName;
				success = true;
			} else {
				dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
				success = true;
			}

			if (dataAccountFetch.id == null) {
				dataAccountFetch = DataAccountFetchServiceProvider.provide().addDataAccountFetch(dataAccountFetch);
			} else {
				dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
			}

			if (dataAccountFetch != null && dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
				ingestedFetchId = futureCall(new IngestDataAccountFetch().name("Add sales"), immediate(dataAccountFetch.id), PipelineSettings.onDataAccountIngestQueue);
			}
		} else {
			LOG.warning(String.format("Gather for data account [%s] and date [%s] skipped because of status [%s]",
					dataAccount.id == null ? dataAccount.username : dataAccount.id.toString(), dateParameter, dataAccountFetch.status));
		}

		return (ingestedFetchId == null || !success) ? null : ingestedFetchId;
	}

	public GatherDataAccountOn name(String value) {
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