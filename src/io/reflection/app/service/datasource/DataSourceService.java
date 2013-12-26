//  
//  DataSourceService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.datasource;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.DataSource;

final class DataSourceService implements IDataSourceService {
	public String getName() {
		return ServiceType.ServiceTypeDataSource.toString();
	}

	@Override
	public DataSource getDataSource(Long id) throws DataAccessException {
		DataSource dataSource = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataSourceConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataSource.toString());

		String getDataSourceQuery = String.format("select * from `dataSource` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			dataSourceConnection.connect();
			dataSourceConnection.executeQuery(getDataSourceQuery);

			if (dataSourceConnection.fetchNextRow()) {
				dataSource = toDataSource(dataSourceConnection);
			}
		} finally {
			if (dataSourceConnection != null) {
				dataSourceConnection.disconnect();
			}
		}
		return dataSource;
	}

	/**
	 * To dataSource
	 * 
	 * @param connection
	 * @return
	 */
	private DataSource toDataSource(Connection connection) throws DataAccessException {
		DataSource dataSource = new DataSource();
		dataSource.id = connection.getCurrentRowLong("id");
		return dataSource;
	}

	@Override
	public DataSource addDataSource(DataSource dataSource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSource updateDataSource(DataSource dataSource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataSource(DataSource dataSource) {
		throw new UnsupportedOperationException();
	}

}