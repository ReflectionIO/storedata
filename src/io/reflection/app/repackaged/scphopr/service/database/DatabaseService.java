//
//  DatabaseService.java
//  repackagables
//
//  Created by William Shakour on Jun 14, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.repackaged.scphopr.service.database;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.service.ServiceType;

import java.util.logging.Logger;

/**
 * @author William Shakour
 *
 */
public class DatabaseService implements IDatabaseService {
	private static final Logger LOG = Logger.getLogger(DatabaseService.class.getName());

	private static final ThreadLocal<Connection> localConnection = new ThreadLocal<Connection>();

	@Override
	public String getName() {
		return ServiceType.ServiceTypeDatabase.toString();
	}

	@Override
	public Connection getConnection(String server, String database, String username, String password) throws DataAccessException {
		Connection connection = localConnection.get();
		if (connection == null) {
			// if (LOG.isLoggable(GaeLevel.DEBUG)) {
			// LOG.log(GaeLevel.DEBUG, "Local thread does not have a connection. Creating one.");
			// }
			connection = new Connection(server, database, username, password);
			localConnection.set(connection);
			// } else {
			// if (LOG.isLoggable(GaeLevel.DEBUG)) {
			// LOG.log(GaeLevel.DEBUG, "Local thread already has a connection. Returning that one.");
			// }
		}

		return connection;
	}

	@Override
	public Connection getNamedConnection(String name) throws DataAccessException {
		Connection connection = null;

		final String server = System.getProperty(DATABASE_SERVER_KEY), database = System.getProperty(DATABASE_CATALOGURE_KEY), username = System
				.getProperty(DATABASE_USERNAME_KEY), password = System.getProperty(DATABASE_PASSWORD_KEY);
		connection = getConnection(server, database, username, password);

		return connection;
	}

	@Override
	public Connection getAdminNamedConnection(String name) throws DataAccessException {
		Connection connection = null;

		final String server = System.getProperty(DATABASE_SERVER_KEY), database = System.getProperty(DATABASE_CATALOGURE_KEY), username = System
				.getProperty(DATABASE_ADMIN_USERNAME_KEY), password = System.getProperty(DATABASE_ADMIN_PASSWORD_KEY);
		connection = getConnection(server, database, username, password);

		return connection;
	}

	@Override
	public Connection getCronNamedConnection(String name) throws DataAccessException {
		Connection connection = null;

		final String server = System.getProperty(DATABASE_SERVER_KEY), database = System.getProperty(DATABASE_CATALOGURE_KEY), username = System
				.getProperty(DATABASE_CRON_USERNAME_KEY), password = System.getProperty(DATABASE_CRON_PASSWORD_KEY);
		connection = getConnection(server, database, username, password);

		return connection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.repackaged.scphopr.service.database.IDatabaseService#realDisconnect()
	 */
	@Override
	public void realDisconnect() {
		final Connection connection = localConnection.get();
		if (connection == null) return;

		try {
			connection.realDisconnect();
		} catch (final DataAccessException e) {
			e.printStackTrace();
		} finally {
			localConnection.remove();
		}
	}
}
