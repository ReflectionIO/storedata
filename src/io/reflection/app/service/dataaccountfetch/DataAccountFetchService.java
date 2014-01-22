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
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import com.google.appengine.api.utils.SystemProperty;

final class DataAccountFetchService implements IDataAccountFetchService {

	// a marker to help deleted rows that are added by developers
	private static final String DEV_PREFIX = "__dev__";

	public String getName() {
		return ServiceType.ServiceTypeDataAccountFetch.toString();
	}

	@Override
	public DataAccountFetch getDataAccountFetch(Long id) throws DataAccessException {
		DataAccountFetch dataAccountFetch = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataAccountFetchConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String getDataAccountFetchQuery = String.format("SELECT * FROM `dataaccountfetch` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
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
	public Boolean isFetchable(DataAccount dataAccount) {

		// for now we are assuming that all accounts are fetchable, if that changes we can add some selection criteria here that limit the number of accounts
		// that we gather e.g. if the last n-gathers on the account have failed. We will probably need a mechanism to access the failure messages - if the 
		// messages are password related then the user might have updated their password: it can get really messy here
		return Boolean.TRUE;
	}

}