//  
//  SessionService.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.session;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class SessionService implements ISessionService {
	public String getName() {
		return ServiceType.ServiceTypeSession.toString();
	}

	@Override
	public Session getSession(Long id) {
		Session session = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection sessionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

		String getSessionQuery = String.format("select * from `session` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			sessionConnection.connect();
			sessionConnection.executeQuery(getSessionQuery);

			if (sessionConnection.fetchNextRow()) {
				session = toSession(sessionConnection);
			}
		} finally {
			if (sessionConnection != null) {
				sessionConnection.disconnect();
			}
		}
		return session;
	}

	/**
	 * To session
	 * 
	 * @param connection
	 * @return
	 */
	private Session toSession(Connection connection) {
		Session session = new Session();
		session.id = connection.getCurrentRowLong("id");
		return session;
	}

	@Override
	public Session addSession(Session session) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Session updateSession(Session session) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSession(Session session) {
		throw new UnsupportedOperationException();
	}

}