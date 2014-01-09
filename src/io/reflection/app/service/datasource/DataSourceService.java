//  
//  DataSourceService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.datasource;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
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

		String getDataSourceQuery = String.format("SELECT * FROM `datasource` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
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
		dataSource.created = connection.getCurrentRowDateTime("created");
		dataSource.deleted = connection.getCurrentRowString("deleted");

		dataSource.a3Code = stripslashes(connection.getCurrentRowString("a3code"));
		dataSource.name = stripslashes(connection.getCurrentRowString("name"));
		dataSource.url = stripslashes(connection.getCurrentRowString("url"));

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.datasource.IDataSourceService#getNamedDataSource(java.lang.String)
	 */
	@Override
	public DataSource getNamedDataSource(String name) throws DataAccessException {
		DataSource dataSource = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataSourceConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataSource.toString());

		String getNamedDataSourceQuery = String.format("SELECT * FROM `datasource` WHERE `deleted`='n' AND `name`='%s' LIMIT 1", addslashes(name));
		try {
			dataSourceConnection.connect();
			dataSourceConnection.executeQuery(getNamedDataSourceQuery);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.datasource.IDataSourceService#getA3CodeDataSource(java.lang.String)
	 */
	@Override
	public DataSource getA3CodeDataSource(String a3Code) throws DataAccessException {
		DataSource dataSource = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection dataSourceConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeDataSource.toString());

		String getNamedDataSourceQuery = String.format("SELECT * FROM `datasource` WHERE `deleted`='n' AND `a3code`='%s' LIMIT 1", addslashes(a3Code));
		try {
			dataSourceConnection.connect();
			dataSourceConnection.executeQuery(getNamedDataSourceQuery);

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

}