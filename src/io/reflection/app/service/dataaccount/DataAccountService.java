//  
//  DataAccountService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.dataaccount;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.DataAccount;

final class DataAccountService implements IDataAccountService {
	public String getName() {
		return ServiceType.ServiceTypeDataAccount.toString();
	}

	@Override
	public DataAccount getDataAccount(Long id) throws DataAccessException {
		DataAccount dataAccount = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataAccountConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataAccount.toString());

		String getDataAccountQuery = String.format("select * from `dataAccount` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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
		return dataAccount;
	}

	@Override
	public DataAccount addDataAccount(DataAccount dataAccount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataAccount updateDataAccount(DataAccount dataAccount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataAccount(DataAccount dataAccount) {
		throw new UnsupportedOperationException();
	}

}