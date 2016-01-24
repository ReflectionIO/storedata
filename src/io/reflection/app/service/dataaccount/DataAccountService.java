//
//  DataAccountService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccount;

import static com.spacehopperstudios.utility.StringUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class DataAccountService implements IDataAccountService {

	private static final Logger	LOG					= Logger.getLogger(DataAccountService.class.getName());

	private static final String	KEY_PART_1	= "F54E1A22";
	private static final String	KEY_PART_2	= "395D";
	private static final String	KEY_PART_3	= "42B8";
	private static final String	KEY_PART_4	= "9002";
	private static final String	KEY_PART_5	= "61E14A750D98";

	@Override
	public String getName() {
		return ServiceType.ServiceTypeDataAccount.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccount(java.lang.Long)
	 */
	@Override
	public DataAccount getDataAccount(Long id) throws DataAccessException {
		return getDataAccount(id, Boolean.FALSE);
	}

	/**
	 *
	 * @param id
	 * @param deleted
	 *          If true, retrieve deleted linked accounts as well
	 * @return
	 * @throws DataAccessException
	 */
	@Override
	public DataAccount getDataAccount(Long id, Boolean deleted) throws DataAccessException {
		DataAccount dataAccount = null;

		final IDatabaseService databaseService = DatabaseServiceProvider.provide();
		final Connection dataAccountConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		final String getDataAccountQuery = String.format(
				"SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `id`=%d %s LIMIT 1", key(),
				id.longValue(), deleted ? "" : "AND `deleted`='n'");

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountQuery);

			if (dataAccountConnection.fetchNextRow()) {
				dataAccount = toDataAccount(dataAccountConnection);
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}
		return dataAccount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccount(java.lang.String, java.lang.Long)
	 */
	@Override
	public DataAccount getDataAccount(String username, Long sourceid) throws DataAccessException {
		return getDataAccount(username, sourceid, Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccount(java.lang.String, java.lang.Long, java.lang.Boolean)
	 */
	@Override
	public DataAccount getDataAccount(String username, Long sourceid, Boolean deleted) throws DataAccessException {
		DataAccount dataAccount = null;

		final IDatabaseService databaseService = DatabaseServiceProvider.provide();
		final Connection dataAccountConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		final String getDataAccountQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `username`='%s' AND `sourceid`=%d %s LIMIT 1",
						key(), username, sourceid, deleted ? "" : "AND `deleted`='n'");

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountQuery);

			if (dataAccountConnection.fetchNextRow()) {
				dataAccount = toDataAccount(dataAccountConnection);
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}
		return dataAccount;
	}

	/**
	 * To dataAccount
	 *
	 * @param connection
	 * @return
	 */
	private DataAccount toDataAccount(Connection connection) throws DataAccessException {
		final DataAccount dataAccount = new DataAccount();
		dataAccount.id = connection.getCurrentRowLong("id");
		dataAccount.created = connection.getCurrentRowDateTime("created");
		dataAccount.deleted = connection.getCurrentRowString("deleted");
		dataAccount.active = connection.getCurrentRowString("active");
		dataAccount.source = new DataSource();
		dataAccount.source.id = connection.getCurrentRowLong("sourceid");

		dataAccount.username = stripslashes(connection.getCurrentRowString("username"));
		dataAccount.password = stripslashes(connection.getCurrentRowString("clearpassword")); // column name is password but all select queries should return
		// decrypted password as clearpassword
		dataAccount.properties = stripslashes(connection.getCurrentRowString("properties"));
		dataAccount.developerName = stripslashes(connection.getCurrentRowString("developer_name"));

		return dataAccount;
	}

	private String key() {
		return KEY_PART_1 + KEY_PART_2 + KEY_PART_3 + KEY_PART_4 + KEY_PART_5;
	}

	@Override
	public DataAccount addDataAccount(DataAccount dataAccount) throws DataAccessException {

		DataAccount addedDataAccount = null;

		final String addDataAccountQuery = String.format(
				"INSERT INTO `dataaccount` (`sourceid`,`username`,`password`,`properties`) VALUES (%d,'%s',AES_ENCRYPT('%s',UNHEX('%s')),'%s')",
				dataAccount.source.id, addslashes(dataAccount.username), addslashes(dataAccount.password), key(), addslashes(dataAccount.properties));

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(addDataAccountQuery);

			if (dataAccountConnection.getAffectedRowCount() > 0) {
				addedDataAccount = getDataAccount(Long.valueOf(dataAccountConnection.getInsertedId()));

				if (addedDataAccount == null) {
					addedDataAccount = dataAccount;
					addedDataAccount.id = Long.valueOf(dataAccountConnection.getInsertedId());
				}
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		if (addedDataAccount != null) {
			enqueue(addedDataAccount, 30, true);
		}

		return addedDataAccount;
	}

	private void enqueue(DataAccount dataAccount, Date date, boolean notify) {
		enqueue(dataAccount.id, date, notify);
	}

	private void enqueue(Long dataAccountId, Date date, boolean notify) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			final Queue queue = QueueFactory.getQueue("dataaccountgather");

			final TaskOptions options = TaskOptions.Builder.withUrl("/dataaccountgather").method(Method.POST);

			options.param("accountId", dataAccountId.toString());
			String dateAsLongString = Long.toString(date.getTime());
			options.param("date", dateAsLongString);

			if (notify) {
				options.param("notify", Boolean.toString(true));
			}

			LOG.log(GaeLevel.DEBUG, String.format("Enqueuing data account gather for account id %s on %s (%s)", dataAccountId, date, dateAsLongString));

			try {
				queue.add(options);
			} catch (final TransientFailureException ex) {

				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
				}

				// retry once
				try {
					queue.add(options);
				} catch (final TransientFailureException reEx) {
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

	private void enqueue(DataAccount dataAccount, int days, boolean requiresNotification) {
		/*
		 * The date we fetch from depends on the time. If it is past 5pm today, we fetch from yesterday and older, else we fetch from day before yesterday
		 */

		final DateTime now = DateTime.now();
		final Date dateToFetchFrom = now.minusDays(now.getHourOfDay() > 17 ? 1 : 2).toDate();

		enqueue(dataAccount, dateToFetchFrom, days, requiresNotification);
	}

	/**
	 * @param dataAccount
	 * @param days
	 */
	private void enqueue(DataAccount dataAccount, Date date, int days, boolean requiresNotification) {
		for (int i = 0; i < days; i++) {
			enqueue(dataAccount, new DateTime(date.getTime(), DateTimeZone.UTC).minusDays(i).toDate(), requiresNotification && days - i == 1);
		}
	}

	@Override
	public DataAccount updateDataAccount(DataAccount dataAccount, boolean collect) throws DataAccessException {

		DataAccount updatedDataAccount = null;

		final String updateDataAccountQuery = String
				.format("UPDATE `dataaccount` SET `username`='%s', `password`=AES_ENCRYPT('%s',UNHEX('%s')), `properties`='%s', `active`='%s' WHERE `id`=%d AND `deleted`='n'",
						addslashes(dataAccount.username), addslashes(dataAccount.password), key(), addslashes(dataAccount.properties),
						addslashes(dataAccount.active), dataAccount.id.longValue());

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(updateDataAccountQuery);

			if (dataAccountConnection.getAffectedRowCount() > 0) {
				updatedDataAccount = getDataAccount(dataAccount.id);
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		if (updatedDataAccount != null && collect) {
			enqueue(updatedDataAccount, 30, false);
		}

		return updatedDataAccount;
	}

	@Override
	public DataAccount restoreDataAccount(DataAccount dataAccount) throws DataAccessException {

		DataAccount restoredDataAccount = null;

		final String restoreDataAccountQuery = String.format(
				"UPDATE `dataaccount` SET `created`=NOW(), `password`=AES_ENCRYPT('%s',UNHEX('%s')), `properties`='%s', `deleted`='n' WHERE `username`='%s'",
				addslashes(dataAccount.password), key(), addslashes(dataAccount.properties), addslashes(dataAccount.username));

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(restoreDataAccountQuery);

			if (dataAccountConnection.getAffectedRowCount() > 0) {
				dataAccount = getDataAccount(dataAccount.username, dataAccount.source.id);
				restoredDataAccount = getDataAccount(dataAccount.id);
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		if (restoredDataAccount != null) {
			enqueue(restoredDataAccount, 30, false);
		}

		return restoredDataAccount;
	}

	@Override
	public void deleteDataAccount(DataAccount dataAccount) throws DataAccessException {
		final String deleteDataAccountQuery = String.format("UPDATE `dataaccount` SET `deleted`='y' WHERE `id`=%d AND `deleted`='n'",
				dataAccount.id.longValue());

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());
		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(deleteDataAccountQuery);

			if (dataAccountConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(String.format("Account with id [%d] was marked as deleted", dataAccount.id.longValue()));
				}
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccounts(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccount> getDataAccounts(Pager pager) throws DataAccessException {
		return getDataAccounts(pager, Boolean.TRUE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getActiveDataAccounts(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccount> getActiveDataAccounts(Pager pager) throws DataAccessException {
		return getDataAccounts(pager, Boolean.FALSE);
	}

	private List<DataAccount> getDataAccounts(Pager pager, Boolean includeInactive) throws DataAccessException {
		final List<DataAccount> dataAccounts = new ArrayList<DataAccount>();

		final String getDataAccountsQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE %s `deleted`='n' ORDER BY `%s` %s LIMIT %d,%d",
						key(), includeInactive == null || !includeInactive.booleanValue() ? "`active`='y' AND" : "", pager.sortBy == null ? "id"
								: stripslashes(pager.sortBy),
						pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
						pager.start == null ? Pager.DEFAULT_START.longValue() : pager.start.longValue(), pager.count == null ? Pager.DEFAULT_COUNT.longValue()
								: pager.count.longValue());

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				final DataAccount dataAccount = toDataAccount(dataAccountConnection);

				if (dataAccount != null) {
					dataAccounts.add(dataAccount);
				}
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		return dataAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccountsCount()
	 */
	@Override
	public Long getDataAccountsCount() throws DataAccessException {
		return getDataAccountsCount(Boolean.TRUE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getActiveDataAccountsCount()
	 */
	@Override
	public Long getActiveDataAccountsCount() throws DataAccessException {
		return getDataAccountsCount(Boolean.FALSE);
	}

	private Long getDataAccountsCount(Boolean includeInactive) throws DataAccessException {
		Long dataAccountsCount = Long.valueOf(0);

		String getDataAccountsCountQuery = String.format("SELECT count(1) AS `count` FROM `dataaccount` WHERE %s `deleted`='n' LIMIT 1",
				includeInactive == null || !includeInactive.booleanValue() ? "`active`='y' AND" : "");

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountsCountQuery);

			if (dataAccountConnection.fetchNextRow()) {
				dataAccountsCount = dataAccountConnection.getCurrentRowLong("count");
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		return dataAccountsCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getIdsDataAccounts(java.util.Collection, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccount> getIdsDataAccounts(Collection<Long> ids, Pager pager) throws DataAccessException {
		final List<DataAccount> dataAccounts = new ArrayList<DataAccount>();

		final StringBuffer joinedIds = new StringBuffer();

		for (final Long id : ids) {
			if (joinedIds.length() != 0) {
				joinedIds.append(",");
			}

			joinedIds.append(id.toString());
		}

		final String getIdsDataAccountsQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `id` in (%s) AND `deleted`='n' ORDER BY `%s` %s",
						key(), joinedIds, pager.sortBy == null ? "id" : stripslashes(pager.sortBy),
						pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC");

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getIdsDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				final DataAccount dataAccount = toDataAccount(dataAccountConnection);

				if (dataAccount != null) {
					dataAccounts.add(dataAccount);
				}
			}

		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		return dataAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getVendorDataAccounts(java.lang.String)
	 */
	@Override
	public List<DataAccount> getVendorDataAccounts(String vendorId, Boolean includeInactive) throws DataAccessException {
		final List<DataAccount> dataAccounts = new ArrayList<DataAccount>();

		final String getVendorDataAccountsQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `properties` LIKE '%%%s%%' %s AND `deleted`='n' ORDER BY `id`",
						key(), vendorId, includeInactive == null || !includeInactive.booleanValue() ? "AND `active`='y'" : "");

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getVendorDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				final DataAccount dataAccount = toDataAccount(dataAccountConnection);

				if (dataAccount != null) {
					dataAccounts.add(dataAccount);
				}
			}

		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		return dataAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#triggerDataAccountFetch(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public void triggerDataAccountFetch(DataAccount dataAccount) throws DataAccessException {
		// TODO: enqueue messages for the number of days since the last success or 30 days - for now we just enqueue the last day
		enqueue(dataAccount, 1, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#triggerSingleDateDataAccountFetch(io.reflection.app.datatypes.shared.DataAccount,
	 * java.util.Date)
	 */
	@Override
	public void triggerSingleDateDataAccountFetch(DataAccount dataAccount, Date date) throws DataAccessException {
		enqueue(dataAccount, date, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#triggerSingleDateDataAccountFetch(io.reflection.app.datatypes.shared.DataAccount,
	 * java.util.Date)
	 */
	@Override
	public void triggerSingleDateDataAccountFetch(Long dataAccountId, Date date) throws DataAccessException {
		enqueue(dataAccountId, date, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#triggerMultipleDateDataAccountFetch(io.reflection.app.datatypes.shared.DataAccount,
	 * java.util.Date, java.lang.Integer)
	 */
	@Override
	public void triggerMultipleDateDataAccountFetch(DataAccount dataAccount, Date date, Integer days) throws DataAccessException {
		enqueue(dataAccount, date, days.intValue(), false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getActiveDataAccountIDs()
	 */
	@Override
	public List<Long> getActiveDataAccountIDs() throws DataAccessException {
		return getDataAccountIds(Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getAllDataAccountIDs()
	 */
	@Override
	public List<Long> getAllDataAccountIDs() throws DataAccessException {
		return getDataAccountIds(Boolean.TRUE);
	}

	private List<Long> getDataAccountIds(Boolean includeInactive) throws DataAccessException {
		final List<Long> dataAccounts = new ArrayList<Long>();

		final String getDataAccountsQuery = String.format("SELECT id FROM `dataaccount` WHERE %s `deleted`='n'",
				includeInactive == null || !includeInactive.booleanValue() ? "`active`='y' AND" : "");

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				dataAccounts.add(dataAccountConnection.getCurrentRowLong("id"));
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		return dataAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccountForUser(java.lang.Long)
	 */
	@Override
	public List<DataAccount> getDataAccountForUser(Long userId) throws DataAccessException {
		final List<DataAccount> dataAccounts = new ArrayList<DataAccount>();

		final String getDataAccountsQuery = String.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` "
				+ " FROM `dataaccount` " + " WHERE " + " `deleted`='n' AND "
				+ " `id` in (select distinct dataaccountid from userdataaccount where deleted='n' and userid=%d)", key(), userId);

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				dataAccounts.add(toDataAccount(dataAccountConnection));
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		return dataAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public DataAccount getDataAccount(String username, String vendor) throws DataAccessException {
		DataAccount dataAccount = null;

		final IDatabaseService databaseService = DatabaseServiceProvider.provide();
		final Connection dataAccountConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		final String getDataAccountQuery = String
				.format(
						"SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `username`='%s' AND `properties` LIKE '%%%s%%' AND `deleted`='n' LIMIT 1",
						key(), username, vendor);

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountQuery);

			if (dataAccountConnection.fetchNextRow()) {
				dataAccount = toDataAccount(dataAccountConnection);
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}
		return dataAccount;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#updateDataAccountProperties(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public void updateDataAccountProperties(DataAccount dataAccount) throws DataAccessException {
		final String updateDataAccountPropertiesQuery = String.format(
				"UPDATE `dataaccount` SET `properties`='%s' WHERE `id`=%d",
				addslashes(dataAccount.properties), dataAccount.id);

		final Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(updateDataAccountPropertiesQuery);
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}
	}
}
