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
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.DataAccount;
import io.reflection.app.shared.datatypes.DataSource;

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

	public String getName() {
		return ServiceType.ServiceTypeDataAccount.toString();
	}

	@Override
	public DataAccount getDataAccount(Long id) throws DataAccessException {
		DataAccount dataAccount = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataAccountConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		String getDataAccountQuery = String.format("select * from `dataaccount` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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
		dataAccount.password = stripslashes(connection.getCurrentRowString("password"));
		dataAccount.properties = stripslashes(connection.getCurrentRowString("properties"));

		return dataAccount;
	}

	@Override
	public DataAccount addDataAccount(DataAccount dataAccount) throws DataAccessException {
		DataAccount addedDataAccount = null;

		final String addDataAccountQuery = String.format(
				"INSERT INTO `dataaccount` (`sourceid`,`username`,`password`,`properties`) VALUES (%d,'%s','%s','%s')", dataAccount.source.id,
				addslashes(dataAccount.username), addslashes(dataAccount.password), addslashes(dataAccount.properties));

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
			enqueue(addedDataAccount, 30);
		}

		return addedDataAccount;
	}

	/**
	 * @param dataAccount
	 * @param days
	 */
	private void enqueue(DataAccount dataAccount, int days) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("dataaccountgather");
			Calendar c = Calendar.getInstance();

			for (int i = 0; i < days; i++) {
				TaskOptions options = TaskOptions.Builder.withUrl("/dataaccountgather").method(Method.POST);

				options.param("accountId", dataAccount.id.toString());

				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH, -i);

				options.param("date", Long.toString(c.getTime().getTime()));

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
	public DataAccount updateDataAccount(DataAccount dataAccount) {
		throw new UnsupportedOperationException();
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
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.dataaccount.IDataAccountService#getDataAccountsCount()
	 */
	@Override
	public Long getDataAccountsCount() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
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

}