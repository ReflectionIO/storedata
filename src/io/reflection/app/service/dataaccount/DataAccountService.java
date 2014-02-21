//  
//  DataAccountService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccount;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;

final class DataAccountService implements IDataAccountService {

	private static final Logger LOG = Logger.getLogger(DataAccountService.class.getName());

	private static final String KEY_PART_1 = "F54E1A22";
	private static final String KEY_PART_2 = "395D";
	private static final String KEY_PART_3 = "42B8";
	private static final String KEY_PART_4 = "9002";
	private static final String KEY_PART_5 = "61E14A750D98";

	public String getName() {
		return ServiceType.ServiceTypeDataAccount.toString();
	}

	@Override
	public DataAccount getDataAccount(Long id) throws DataAccessException {
		DataAccount dataAccount = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataAccountConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		String getDataAccountQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `deleted`='n' AND `id`='%d' LIMIT 1",
						key(), id.longValue());

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
		DataAccount dataAccount = new DataAccount();
		dataAccount.id = connection.getCurrentRowLong("id");
		dataAccount.created = connection.getCurrentRowDateTime("created");
		dataAccount.deleted = connection.getCurrentRowString("deleted");

		dataAccount.source = new DataSource();
		dataAccount.source.id = connection.getCurrentRowLong("sourceid");

		dataAccount.username = stripslashes(connection.getCurrentRowString("username"));
		dataAccount.password = stripslashes(connection.getCurrentRowString("clearpassword")); // column name is password but all select queries should return
																								// decrypted password as clearpassword
		dataAccount.properties = stripslashes(connection.getCurrentRowString("properties"));

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

		Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

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

	/**
	 * @param dataAccount
	 * @param days
	 */
	private void enqueue(DataAccount dataAccount, int days, boolean add) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("dataaccountgather");
			Calendar c = Calendar.getInstance();

			for (int i = 1; i <= days; i++) {
				TaskOptions options = TaskOptions.Builder.withUrl("/dataaccountgather").method(Method.POST);

				options.param("accountId", dataAccount.id.toString());

				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH, -i);

				options.param("date", Long.toString(c.getTime().getTime()));
				
				if (add && i == days) {
					options.param("notify", Boolean.toString(true));
				}

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
									String.format("Retry of with payload [%s] failed while adding to queue [%s] twice", options.toString(),
											queue.getQueueName()), reEx);
						}
					}
				}
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

	@Override
	public DataAccount updateDataAccount(DataAccount dataAccount) throws DataAccessException {

		DataAccount updDataAccount = null;

		// TODO: the username is unique and it isn't possible to update it

		final String updDataAccountQuery = String.format(
				"UPDATE `dataaccount` SET `password` = AES_ENCRYPT('%s',UNHEX('%s')), `properties`='%s' WHERE `id`='%d'", addslashes(dataAccount.password),
				key(), addslashes(dataAccount.properties), dataAccount.source.id);

		Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(updDataAccountQuery);

			if (dataAccountConnection.getAffectedRowCount() > 0) {
				updDataAccount = getDataAccount(Long.valueOf(dataAccountConnection.getInsertedId()));

				if (updDataAccount == null) {
					updDataAccount = dataAccount;
					updDataAccount.id = Long.valueOf(dataAccountConnection.getInsertedId());
				}
			}
		} finally {
			if (dataAccountConnection != null) {
				dataAccountConnection.disconnect();
			}
		}

		if (updDataAccount != null) {
			enqueue(updDataAccount, 30, false);
		}

		return updDataAccount;
	}

	@Override
	public void deleteDataAccount(DataAccount dataAccount) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccounts(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccount> getDataAccounts(Pager pager) throws DataAccessException {

		List<DataAccount> dataAccounts = new ArrayList<DataAccount>();

		String getDataAccountsQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `deleted`='n' ORDER BY `%s` %s LIMIT %d,%d",
						key(), pager.sortBy == null ? "id" : pager.sortBy,
						pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

		Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				DataAccount dataAccount = toDataAccount(dataAccountConnection);

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
		Long dataAccountsCount = Long.valueOf(0);

		String getDataAccountsCountQuery = String.format("SELECT count(1) AS `count` FROM `dataaccount` WHERE `deleted`='n' LIMIT 1");

		Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

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
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#addDataAccount(io.reflection.app.shared.datatypes.DataSource, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DataAccount addDataAccount(DataSource dataSource, String username, String password, String properties) throws DataAccessException {
		DataAccount dataAccount = new DataAccount();

		dataAccount.source = dataSource;
		dataAccount.username = username;
		dataAccount.password = password;
		dataAccount.properties = properties;

		return addDataAccount(dataAccount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getIdsDataAccounts(java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccount> getIdsDataAccounts(List<Long> ids, Pager pager) throws DataAccessException {
		List<DataAccount> dataAccounts = new ArrayList<DataAccount>();

		StringBuffer joinedIds = new StringBuffer();

		for (Long id : ids) {
			if (joinedIds.length() != 0) {
				joinedIds.append(",");
			}

			joinedIds.append(id.toString());
		}

		String getIdsDataAccountsQuery = String
				.format("SELECT *, convert(aes_decrypt(`password`,UNHEX('%s')), CHAR(1000)) AS `clearpassword` FROM `dataaccount` WHERE `id` in (%s) AND `deleted`='n' ORDER BY `%s` %s LIMIT %d,%d",
						key(), joinedIds, pager.sortBy == null ? "id" : pager.sortBy,
						pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
						pager.start == null ? 0 : pager.start.longValue(), pager.count == null ? 25 : pager.count.longValue());

		Connection dataAccountConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		try {
			dataAccountConnection.connect();
			dataAccountConnection.executeQuery(getIdsDataAccountsQuery);

			while (dataAccountConnection.fetchNextRow()) {
				DataAccount dataAccount = toDataAccount(dataAccountConnection);

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
	public void triggerDataAccountFetch(DataAccount dataAccount) {
		// TODO: enqueue messages for the number of days since the last success or 30 days - for now we just enqueue the last day
		enqueue(dataAccount, 1, false);
	}

}