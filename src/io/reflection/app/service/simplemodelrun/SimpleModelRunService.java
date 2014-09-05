//  
//  SimpleModelRunService.java
//  reflection.io
//
//  Created by William Shakour on September 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.simplemodelrun;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class SimpleModelRunService implements ISimpleModelRunService {
	public String getName() {
		return ServiceType.ServiceTypeSimpleModelRun.toString();
	}

	@Override
	public SimpleModelRun getSimpleModelRun(Long id) throws DataAccessException {
		SimpleModelRun simpleModelRun = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection simpleModelRunConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSimpleModelRun.toString());

		String getSimpleModelRunQuery = String.format("SELECT * FROM `simplemodelrun` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			simpleModelRunConnection.connect();
			simpleModelRunConnection.executeQuery(getSimpleModelRunQuery);

			if (simpleModelRunConnection.fetchNextRow()) {
				simpleModelRun = toSimpleModelRun(simpleModelRunConnection);
			}
		} finally {
			if (simpleModelRunConnection != null) {
				simpleModelRunConnection.disconnect();
			}
		}
		return simpleModelRun;
	}

	/**
	 * To simpleModelRun
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private SimpleModelRun toSimpleModelRun(Connection connection) throws DataAccessException {
		SimpleModelRun simpleModelRun = new SimpleModelRun();

		simpleModelRun.id = connection.getCurrentRowLong("id");
		simpleModelRun.created = connection.getCurrentRowDateTime("created");
		simpleModelRun.deleted = connection.getCurrentRowString("deleted");

		simpleModelRun.country = connection.getCurrentRowString("country");
		simpleModelRun.store = connection.getCurrentRowString("store");

		simpleModelRun.category = new Category();
		simpleModelRun.category.id = connection.getCurrentRowLong("categoryid");

		simpleModelRun.code = connection.getCurrentRowLong("code");
		simpleModelRun.form = FormType.fromString(connection.getCurrentRowString("form"));
		simpleModelRun.a = connection.getCurrentRowDouble("a");
		simpleModelRun.b = connection.getCurrentRowDouble("b");

		return simpleModelRun;
	}

	@Override
	public SimpleModelRun addSimpleModelRun(SimpleModelRun simpleModelRun) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleModelRun updateSimpleModelRun(SimpleModelRun simpleModelRun) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSimpleModelRun(SimpleModelRun simpleModelRun) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}