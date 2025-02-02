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
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

		String storeCodes = connection.getCurrentRowString("stores");
		if (storeCodes != null) {
			dataSource.stores = Arrays.asList(stripslashes(storeCodes).split(","));
		}

		return dataSource;
	}

	@Override
	public DataSource addDataSource(DataSource dataSource) {
		// LATER Auto-generated method stub addDataSource
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSource updateDataSource(DataSource dataSource) {
		// LATER Auto-generated method stub updateDataSource
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataSource(DataSource dataSource) {
		// LATER Auto-generated method stub deleteDataSource
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.datasource.IDataSourceService#getDataSourceBatch(java.util.List)
	 */
	@Override
	public List<DataSource> getDataSourceBatch(Collection<Long> dataSourceIds) throws DataAccessException {
		List<DataSource> dataSources = new ArrayList<DataSource>();

		StringBuffer commaDelimitedDataSourceIds = new StringBuffer();

		if (dataSourceIds != null && dataSourceIds.size() > 0) {
			for (Long dataSourceId : dataSourceIds) {
				if (commaDelimitedDataSourceIds.length() != 0) {
					commaDelimitedDataSourceIds.append("','");
				}

				commaDelimitedDataSourceIds.append(dataSourceId);
			}
		}

		if (commaDelimitedDataSourceIds != null && commaDelimitedDataSourceIds.length() != 0) {
			String getDataSourceBatchQuery = String.format("SELECT * FROM `datasource` WHERE `deleted`='n' AND `id` IN ('%s')", commaDelimitedDataSourceIds);

			Connection dataDourceConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeDataSource.toString());

			try {
				dataDourceConnection.connect();
				dataDourceConnection.executeQuery(getDataSourceBatchQuery);

				while (dataDourceConnection.fetchNextRow()) {
					DataSource dataSource = toDataSource(dataDourceConnection);

					if (dataSource != null) {
						dataSources.add(dataSource);
					}
				}
			} finally {
				if (dataDourceConnection != null) {
					dataDourceConnection.disconnect();
				}
			}
		}

		return dataSources;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.datasource.IDataSourceService#getStoreDataSource(io.reflection.app.datatypes.shared.Store)
	 */
	@Override
	public DataSource getStoreDataSource(Store store) throws DataAccessException {
		DataSource dataSource = null;

		switch (store.a3Code) {
		case DataTypeHelper.IOS_STORE_A3:
			dataSource = getA3CodeDataSource(DataTypeHelper.ITC_SOURCE_A3);
			break;
		}

		return dataSource;
	}

}