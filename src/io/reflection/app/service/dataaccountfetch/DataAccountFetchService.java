//  
//  DataAccountFetchService.java
//  reflection.io
//
//  Created by William Shakour on January 9, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccountfetch;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.DataAccountFetch;

final class DataAccountFetchService implements IDataAccountFetchService {
	public String getName() {
		return ServiceType.ServiceTypeDataAccountFetch.toString();
	}

	@Override
	public DataAccountFetch getDataAccountFetch(Long id) throws DataAccessException {
		DataAccountFetch dataAccountFetch = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataAccountFetchConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccountFetch.toString());

		String getDataAccountFetchQuery = String.format("select * from `dataAccountFetch` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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
		return dataAccountFetch;
	}

	@Override
	public DataAccountFetch addDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataAccountFetch updateDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataAccountFetch(DataAccountFetch dataAccountFetch) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}