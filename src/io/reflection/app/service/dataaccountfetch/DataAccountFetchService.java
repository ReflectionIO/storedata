//  
//  DataAccountFetchService.java
//  reflection.io
//
//  Created by William Shakour on January 9, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccountfetch;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.utils.SystemProperty;

final class DataAccountFetchService implements IDataAccountFetchService {

	private static final Logger LOG = Logger.getLogger(DataAccountFetchService.class.getName());
	
	// a marker to help deleted rows that are added by developers
	private static final String DEV_PREFIX = "__dev__";

	public String getName() {
		return ServiceType.ServiceTypeDataAccountFetch.toString();
	}

	@Override
	public DataAccountFetch getDataAccountFetch(Long id) throws DataAccessException {
		DataAccountFetch dataAccountFetch = null;

		Connection dataAccountFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String getDataAccountFetchQuery = String.format("SELECT * FROM `dataaccountfetch` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			dataAccountFetchConnection.connect();
			dataAccountFetchConnection.executeQuery(getDataAccountFetchQuery);

			if (dataAccountFetchConnection.fetchNextRow()) {
				dataAccountFetch = toDataAccountFetch(dataAccountFetchConnection);
			}
		} finally {
			if (dataAccountFetchConnection != null) {
				dataAccountFetchConnection.disconnect();
			}
		}
		return dataAccountFetch;
	}

	/**
	 * To dataAccountFetch
	 * 
	 * @param connection
	 * @return
	 */
	private DataAccountFetch toDataAccountFetch(Connection connection) throws DataAccessException {
		DataAccountFetch dataAccountFetch = new DataAccountFetch();

		dataAccountFetch.id = connection.getCurrentRowLong("id");
		dataAccountFetch.created = connection.getCurrentRowDateTime("created");
		dataAccountFetch.deleted = connection.getCurrentRowString("deleted");

		dataAccountFetch.data = stripslashes(connection.getCurrentRowString("data"));

		// remove the dev prefix if the path is on someone's local machine
		if (dataAccountFetch.data != null && dataAccountFetch.data.startsWith(DEV_PREFIX)) {
			dataAccountFetch.data = dataAccountFetch.data.replaceFirst(DEV_PREFIX, "");
		}

		dataAccountFetch.date = connection.getCurrentRowDateTime("date");
		dataAccountFetch.status = DataAccountFetchStatusType.fromString(connection.getCurrentRowString("status"));

		dataAccountFetch.linkedAccount = new DataAccount();
		dataAccountFetch.linkedAccount.id = connection.getCurrentRowLong("linkedaccountid");

		return dataAccountFetch;
	}

	@Override
	public DataAccountFetch addDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException {
		DataAccountFetch addedDataAccountFetch = null;

		final String addDataAccountFetchQuery = String.format(
				"INSERT INTO `dataaccountfetch` (`data`,`date`,`status`,`linkedaccountid`) VALUES ('%s',FROM_UNIXTIME(%d),'%s',%d)", SystemProperty.environment
						.value() == SystemProperty.Environment.Value.Development ? DEV_PREFIX + addslashes(dataAccountFetch.data)
						: addslashes(dataAccountFetch.data), dataAccountFetch.date.getTime() / 1000, dataAccountFetch.status.toString(),
				dataAccountFetch.linkedAccount.id.longValue());

		Connection dataAccountFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		try {
			dataAccountFetchConnection.connect();
			dataAccountFetchConnection.executeQuery(addDataAccountFetchQuery);

			if (dataAccountFetchConnection.getAffectedRowCount() > 0) {
				addedDataAccountFetch = getDataAccountFetch(Long.valueOf(dataAccountFetchConnection.getInsertedId()));

				if (addedDataAccountFetch == null) {
					addedDataAccountFetch = dataAccountFetch;
					addedDataAccountFetch.id = Long.valueOf(dataAccountFetchConnection.getInsertedId());
				}
			}
		} finally {
			if (dataAccountFetchConnection != null) {
				dataAccountFetchConnection.disconnect();
			}
		}

		return addedDataAccountFetch;
	}

	@Override
	public DataAccountFetch updateDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException {
		DataAccountFetch updatedDataAccountFetch = null;

		final String updateDataAccountFetchQuery = String.format(
				"UPDATE `dataaccountfetch` SET `data`='%s',`date`=FROM_UNIXTIME(%d),`status`='%s',`linkedaccountid`=%d WHERE `id`=%d",
				addslashes(dataAccountFetch.data), dataAccountFetch.date.getTime() / 1000, dataAccountFetch.status.toString(),
				dataAccountFetch.linkedAccount.id.longValue(), dataAccountFetch.id.longValue());

		Connection dataAccountFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		try {
			dataAccountFetchConnection.connect();
			dataAccountFetchConnection.executeQuery(updateDataAccountFetchQuery);

			if (dataAccountFetchConnection.getAffectedRowCount() > 0) {
				updatedDataAccountFetch = getDataAccountFetch(dataAccountFetch.id);
			} else {
				updatedDataAccountFetch = dataAccountFetch;
			}
		} finally {
			if (dataAccountFetchConnection != null) {
				dataAccountFetchConnection.disconnect();
			}
		}

		return updatedDataAccountFetch;
	}

	@Override
	public void deleteDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#isFetchable(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public Boolean isFetchable(DataAccount dataAccount) throws DataAccessException {

		// for now we are assuming that all accounts are fetchable, if that changes we can add some selection criteria here that limit the number of accounts
		// that we gather e.g. if the last n-gathers on the account have failed. We will probably need a mechanism to access the failure messages - if the
		// messages are password related then the user might have updated their password: it can get really messy here
		return Boolean.TRUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getFailedDataAccountFetches(io.reflection.app.datatypes.shared.DataAccount,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccountFetch> getFailedDataAccountFetches(DataAccount dataAccount, Pager pager) throws DataAccessException {
		List<DataAccountFetch> failedDataAccountFetches = new ArrayList<DataAccountFetch>();

		Connection dataAccountFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String getFailedDataAccountFetchesQuery = String
				.format("SELECT * FROM `dataaccountfetch` WHERE `linkedaccountid`=%d AND `status`='error' AND `date` BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() AND `deleted`='n'",
						dataAccount.id.longValue());

		if (pager != null) {
			String sortByQuery = "id";

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
				case SortDirectionTypeAscending:
					sortDirectionQuery = "ASC";
					break;
				default:
					break;
				}
			}

			getFailedDataAccountFetchesQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getFailedDataAccountFetchesQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getFailedDataAccountFetchesQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		try {
			dataAccountFetchConnection.connect();
			dataAccountFetchConnection.executeQuery(getFailedDataAccountFetchesQuery);

			while (dataAccountFetchConnection.fetchNextRow()) {
				DataAccountFetch dataAccountFetch = this.toDataAccountFetch(dataAccountFetchConnection);

				if (dataAccountFetch != null) {
					failedDataAccountFetches.add(dataAccountFetch);
				}
			}
		} finally {
			if (dataAccountFetchConnection != null) {
				dataAccountFetchConnection.disconnect();
			}
		}

		return failedDataAccountFetches;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getFailedDataAccountFetchesCount(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public Long getFailedDataAccountFetchesCount(DataAccount dataAccount) throws DataAccessException {
		Long failedDataAccountFetchesCount = Long.valueOf(0);

		String getFailedDataAccountFetchesCountQuery = String
				.format("SELECT COUNT(1) AS `count` FROM `dataaccountfetch` WHERE `linkedaccountid`=%d AND `status`='error' AND `date` BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() AND `deleted`='n'",
						dataAccount.id.longValue());

		Connection dataAccountFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		try {
			dataAccountFetchConnection.connect();
			dataAccountFetchConnection.executeQuery(getFailedDataAccountFetchesCountQuery);

			if (dataAccountFetchConnection.fetchNextRow()) {
				failedDataAccountFetchesCount = dataAccountFetchConnection.getCurrentRowLong("count");
			}
		} finally {
			if (dataAccountFetchConnection != null) {
				dataAccountFetchConnection.disconnect();
			}
		}

		return failedDataAccountFetchesCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#getDateDataAccountFetch(io.reflection.app.datatypes.shared.DataAccount,
	 * java.util.Date)
	 */
	@Override
	public DataAccountFetch getDateDataAccountFetch(DataAccount dataAccount, Date date) throws DataAccessException {
		DataAccountFetch dataAccountFetch = null;

		Connection dataAccountFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String getDateDataAccountFetchQuery = String.format(
				"SELECT * FROM `dataaccountfetch` WHERE `linkedaccountid`=%d AND `date`=FROM_UNIXTIME(%d) AND `deleted`='n' LIMIT 1",
				dataAccount.id.longValue(), date.getTime() / 1000);
		try {
			dataAccountFetchConnection.connect();
			dataAccountFetchConnection.executeQuery(getDateDataAccountFetchQuery);

			if (dataAccountFetchConnection.fetchNextRow()) {
				dataAccountFetch = toDataAccountFetch(dataAccountFetchConnection);
			}
		} finally {
			if (dataAccountFetchConnection != null) {
				dataAccountFetchConnection.disconnect();
			}
		}
		return dataAccountFetch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#getDataAccountFetches(io.reflection.app.datatypes.shared.DataAccount,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccountFetch> getDataAccountFetches(DataAccount dataAccount, Date start, Date end, Pager pager) throws DataAccessException {
		List<DataAccountFetch> dataAccountFetches = new ArrayList<DataAccountFetch>();

		Connection dataAccountFetchesConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String linkedAccountPart = "";
		if (dataAccount != null && dataAccount.id != null) {
			linkedAccountPart = "`linkedaccountid`=" + dataAccount.id.longValue() + " AND";
		}
		String getDataAccountFetchesQuery = String.format("SELECT * FROM `dataaccountfetch` WHERE %s %s `deleted`='n'",
				SqlQueryHelper.getBeforeAfterQuery(end, start), linkedAccountPart);

		if (pager != null) {
			String sortByQuery = null;
			if (dataAccount != null && dataAccount.id != null) {
				sortByQuery = "id";
			} else {
				sortByQuery = "linkedaccountid";
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
				case SortDirectionTypeAscending:
					sortDirectionQuery = "ASC";
					break;
				default:
					break;
				}
			}

			getDataAccountFetchesQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getDataAccountFetchesQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getDataAccountFetchesQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		try {
			dataAccountFetchesConnection.connect();
			dataAccountFetchesConnection.executeQuery(getDataAccountFetchesQuery);

			while (dataAccountFetchesConnection.fetchNextRow()) {
				DataAccountFetch dataAccountFetch = this.toDataAccountFetch(dataAccountFetchesConnection);

				if (dataAccountFetch != null) {
					dataAccountFetches.add(dataAccountFetch);
				}
			}
		} finally {
			if (dataAccountFetchesConnection != null) {
				dataAccountFetchesConnection.disconnect();
			}
		}

		return dataAccountFetches;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#getDataAccountFetches(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccountFetch> getDataAccountFetches(Date start, Date end, Pager pager) throws DataAccessException {
		return getDataAccountFetches(null, start, end, pager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#getDataAccountFetchesCount(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public Long getDataAccountFetchesCount(DataAccount dataAccount, Date start, Date end) throws DataAccessException {
		Long dataAccountFetchesCount = Long.valueOf(0);

		Connection dataAccountFetchesCountConnection = DatabaseServiceProvider.provide().getNamedConnection(
				DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String linkedAccountPart = "";
		if (dataAccount != null && dataAccount.id != null) {
			linkedAccountPart = "`linkedaccountid`=" + dataAccount.id.longValue() + " AND";
		}
		String getDataAccountFetchesQuery = String.format("SELECT COUNT(1) AS `count` FROM `dataaccountfetch` WHERE %s %s `deleted`='n'",
				SqlQueryHelper.getBeforeAfterQuery(end, start), linkedAccountPart);

		try {
			dataAccountFetchesCountConnection.connect();
			dataAccountFetchesCountConnection.executeQuery(getDataAccountFetchesQuery);

			if (dataAccountFetchesCountConnection.fetchNextRow()) {
				dataAccountFetchesCount = dataAccountFetchesCountConnection.getCurrentRowLong("count");
			}
		} finally {
			if (dataAccountFetchesCountConnection != null) {
				dataAccountFetchesCountConnection.disconnect();
			}
		}

		return dataAccountFetchesCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#getDataAccountFetchesCount()
	 */
	@Override
	public Long getDataAccountFetchesCount(Date start, Date end) throws DataAccessException {
		return getDataAccountFetchesCount(null, start, end);
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.dataaccountfetch.IDataAccountFetchService#triggerDataAccountFetchIngest(io.reflection.app.datatypes.shared.DataAccountFetch)
	 */
	@Override
	public void triggerDataAccountFetchIngest(DataAccountFetch fetch) throws DataAccessException {
		enqueue(fetch);
	}
	
	/**
	 * 
	 * @param fetch
	 */
	private void enqueue(DataAccountFetch fetch) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("dataaccountingest");

			TaskOptions options = TaskOptions.Builder.withUrl("/dataaccountingest").method(Method.POST);

			options.param("fetchId", fetch.id.toString());

			try {
				queue.add(options);
			} catch (TransientFailureException ex) {

				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
				}

				// retry once
				try {
					queue.add(options);
				} catch (TransientFailureException reEx) {
					if (LOG.isLoggable(Level.SEVERE)) {
						LOG.log(Level.SEVERE,
								String.format("Retry of with payload [%s] failed while adding to queue [%s] twice", options.toString(), queue.getQueueName()),
								reEx);
					}
				}

			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

}