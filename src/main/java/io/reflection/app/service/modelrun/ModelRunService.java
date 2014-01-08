//  
//  ModelRunService.java
//  reflection.io
//
//  Created by William Shakour on October 23, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.modelrun;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.FormType;
import io.reflection.app.shared.datatypes.ModelRun;
import io.reflection.app.shared.datatypes.Store;

final class ModelRunService implements IModelRunService {
	public String getName() {
		return ServiceType.ServiceTypeModelRun.toString();
	}

	@Override
	public ModelRun getModelRun(Long id) throws DataAccessException {
		ModelRun modelRun = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection modelRunConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeModelRun.toString());

		final String getModelRunQuery = String.format("SELECT * FROM `modelrun` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());

		try {
			modelRunConnection.connect();
			modelRunConnection.executeQuery(getModelRunQuery);

			if (modelRunConnection.fetchNextRow()) {
				modelRun = toModelRun(modelRunConnection);
			}
		} finally {
			if (modelRunConnection != null) {
				modelRunConnection.disconnect();
			}
		}
		return modelRun;
	}

	/**
	 * To modelRun
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private ModelRun toModelRun(Connection connection) throws DataAccessException {
		ModelRun modelRun = new ModelRun();

		modelRun.id = connection.getCurrentRowLong("id");
		modelRun.created = connection.getCurrentRowDateTime("created");
		modelRun.deleted = connection.getCurrentRowString("deleted");

		modelRun.country = stripslashes(connection.getCurrentRowString("country"));
		modelRun.store = stripslashes(connection.getCurrentRowString("store"));

		modelRun.code = stripslashes(connection.getCurrentRowString("code"));
		modelRun.form = FormType.fromString(connection.getCurrentRowString("form"));
		modelRun.grossingA = connection.getCurrentRowDouble("grossinga");
		modelRun.paidA = connection.getCurrentRowDouble("paida");
		modelRun.bRatio = connection.getCurrentRowDouble("bratio");
		modelRun.totalDownloads = connection.getCurrentRowDouble("totaldownloads");
		modelRun.paidB = connection.getCurrentRowDouble("paidb");
		modelRun.grossingB = connection.getCurrentRowDouble("grossingb");
		modelRun.paidAIap = connection.getCurrentRowDouble("paidaiap");
		modelRun.grossingAIap = connection.getCurrentRowDouble("grossingaiap");
		modelRun.freeA = connection.getCurrentRowDouble("freea");
		modelRun.theta = connection.getCurrentRowDouble("theta");
		modelRun.freeB = connection.getCurrentRowDouble("freeb");

		return modelRun;
	}

	@Override
	public ModelRun addModelRun(ModelRun modelRun) throws DataAccessException {
		ModelRun addedModelRun = null;

		final String addModelRunQuery = String
				.format("INSERT INTO `modelrun` (`country`,`store`,`code`,`form`,`grossinga`,`paida`,`bratio`,`totaldownloads`,`paidb`,`grossingb`,`paidaiap`,`grossingaiap`,`freea`,`theta`,`freeb`) VALUES ('%s','%s','%s','%s',%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f);",
						addslashes(modelRun.country), addslashes(modelRun.store), addslashes(modelRun.code), modelRun.form.toString(),
						modelRun.grossingA.doubleValue(), modelRun.paidA.doubleValue(), modelRun.bRatio.doubleValue(), modelRun.totalDownloads.doubleValue(),
						modelRun.paidB.doubleValue(), modelRun.grossingB.doubleValue(), modelRun.paidAIap.doubleValue(), modelRun.grossingAIap.doubleValue(),
						modelRun.freeA.doubleValue(), modelRun.theta.doubleValue(), modelRun.freeB.doubleValue());

		Connection modelRunConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeModelRun.toString());

		try {
			modelRunConnection.connect();
			modelRunConnection.executeQuery(addModelRunQuery);

			if (modelRunConnection.getAffectedRowCount() > 0) {
				addedModelRun = getModelRun(Long.valueOf(modelRunConnection.getInsertedId()));

				if (addedModelRun == null) {
					addedModelRun = modelRun;
					addedModelRun.id = Long.valueOf(modelRunConnection.getInsertedId());
				}
			}
		} finally {
			if (modelRunConnection != null) {
				modelRunConnection.disconnect();
			}
		}

		return addedModelRun;
	}

	@Override
	public ModelRun updateModelRun(ModelRun modelRun) throws DataAccessException {
		ModelRun updatedModelRun = null;

		final String updateModelRunQuery = String
				.format("UPDATE `modelrun` SET `country`='%s',`store`='%s',`code`='%s',`form`='%s',`grossinga`=%f,`paida`=%f,`bratio`=%f,`totaldownloads`=%f,`paidb`=%f,`grossingb`=%f,`paidaiap`=%f,`grossingaiap`=%f,`freea`=%f,`theta`=%f,`freeb`=%f WHERE `id`=%d AND `deleted`='n';",
						addslashes(modelRun.country), addslashes(modelRun.store), addslashes(modelRun.code), modelRun.form.toString(),
						modelRun.grossingA.doubleValue(), modelRun.paidA.doubleValue(), modelRun.bRatio.doubleValue(), modelRun.totalDownloads.doubleValue(),
						modelRun.paidB.doubleValue(), modelRun.grossingB.doubleValue(), modelRun.paidAIap.doubleValue(), modelRun.grossingAIap.doubleValue(),
						modelRun.freeA.doubleValue(), modelRun.theta.doubleValue(), modelRun.freeB.doubleValue(), modelRun.id.longValue());

		Connection modelRunConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeModelRun.toString());

		try {
			modelRunConnection.connect();
			modelRunConnection.executeQuery(updateModelRunQuery);

			if (modelRunConnection.getAffectedRowCount() > 0) {
				updatedModelRun = getModelRun(modelRun.id);
			} else {
				updatedModelRun = modelRun;
			}

		} finally {
			if (modelRunConnection != null) {
				modelRunConnection.disconnect();
			}
		}

		return updatedModelRun;
	}

	@Override
	public void deleteModelRun(ModelRun modelRun) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.modelrun.IModelRunService#getGatherCodeModelRun(java.lang.String, java.lang.String,
	 * io.reflection.app.shared.datatypes.FormType, java.lang.String)
	 */
	@Override
	public ModelRun getGatherCodeModelRun(Country country, Store store, FormType form, String code) throws DataAccessException {
		ModelRun modelRun = null;

		final String getGatherCodeModelRunQuery = String.format(
				"SELECT * FROM `modelrun` WHERE `store`='%s' AND `country`='%s' AND `form`='%s' AND `code`='%s'", addslashes(store.a3Code),
				addslashes(country.a2Code), form.toString(), addslashes(code));

		Connection modelRunConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeModelRun.toString());

		try {
			modelRunConnection.connect();
			modelRunConnection.executeQuery(getGatherCodeModelRunQuery);

			if (modelRunConnection.fetchNextRow()) {
				modelRun = toModelRun(modelRunConnection);
			}
		} finally {
			if (modelRunConnection != null) {
				modelRunConnection.disconnect();
			}
		}

		return modelRun;
	}

}