//  
//  SessionService.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.session;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.logging.Level;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

final class SessionService implements ISessionService {

	private MemcacheService syncCache;
	private AsyncMemcacheService asyncCache;

	public SessionService() {
		syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));

		asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
		asyncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));
	}

	public String getName() {
		return ServiceType.ServiceTypeSession.toString();
	}

	@Override
	public Session getSession(Long id) throws DataAccessException {
		Session session = null;

		String memcacheKey = getName() + ".id." + id;
		String jsonString = (String) syncCache.get(memcacheKey);

		if (jsonString == null) {
			IDatabaseService databaseService = DatabaseServiceProvider.provide();
			Connection sessionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

			String getSessionQuery = String
					.format("SELECT *, CAST(`token` AS CHAR) AS `chartoken` FROM `session` WHERE `id`='%d' AND `expires` > NOW() AND `deleted`='n' ORDER BY `expires` DESC LIMIT 1",
							id.longValue());
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

			if (session != null) {
				String json = session.toString();
				asyncCache.put(memcacheKey, json);

				memcacheKey = getName() + ".userid." + session.user.id;
				asyncCache.put(memcacheKey, json);

				memcacheKey = getName() + ".token." + session.token;
				asyncCache.put(memcacheKey, json);
			}
		} else {
			session = new Session();
			session.fromJson(jsonString);
			
			if (session.expires != null && new DateTime(session.expires, DateTimeZone.UTC).isBeforeNow()) {
				asyncCache.delete(memcacheKey);
				session = null;
			}
		}

		return session;
	}

	/**
	 * To session
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private Session toSession(Connection connection) throws DataAccessException {
		Session session = new Session();

		session.id = connection.getCurrentRowLong("id");
		session.created = connection.getCurrentRowDateTime("created");
		session.deleted = connection.getCurrentRowString("deleted");

		session.expires = connection.getCurrentRowDateTime("expires");
		session.token = connection.getCurrentRowString("chartoken");
		session.user = new User();
		session.user.id = connection.getCurrentRowLong("userid");

		return session;
	}

	@Override
	public Session addSession(Session session) throws DataAccessException {
		return createUserSession(session.user, false);
	}

	@Override
	public Session updateSession(Session session) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSession(Session session) throws DataAccessException {
		Connection sessionConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

		String deleteSessionQuery = String.format("UPDATE `session` SET `deleted`='y' WHERE `id`=%d", session.id.longValue());
		try {
			sessionConnection.connect();
			sessionConnection.executeQuery(deleteSessionQuery);

			String memcacheKey = getName() + ".id." + session.id;
			asyncCache.delete(memcacheKey);
		} finally {
			if (sessionConnection != null) {
				sessionConnection.disconnect();
			}
		}
	}

	@Override
	public Session createUserSession(User user, Boolean longTerm) throws DataAccessException {
		Session session = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection sessionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

		String createUserSessionQuery = String.format(
				"INSERT INTO `session` (`userid`, `token`, `expires`) VALUES (%d, CAST(UUID() AS BINARY), date_add(now(), INTERVAL %d SECOND))",
				user.id.longValue(), longTerm == Boolean.TRUE ? SESSION_LONG_DURATION : SESSION_SHORT_DURATION);

		try {
			sessionConnection.connect();
			sessionConnection.executeQuery(createUserSessionQuery);

			if (sessionConnection.getAffectedRowCount() > 0) {
				long sessionId = sessionConnection.getInsertedId();

				String memcacheKey = getName() + ".id." + sessionId;
				syncCache.delete(memcacheKey);

				session = getSession(sessionId);
			}
		} finally {
			if (sessionConnection != null) {
				sessionConnection.disconnect();
			}
		}

		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.session.ISessionService#getUserSession(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public Session getUserSession(User user) throws DataAccessException {
		Session session = null;

		String memcacheKey = getName() + ".userid." + user.id;
		String jsonString = (String) syncCache.get(memcacheKey);

		if (jsonString == null) {
			IDatabaseService databaseService = DatabaseServiceProvider.provide();
			Connection sessionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

			String getUserSessionQuery = String
					.format("SELECT *, CAST(`token` AS CHAR) AS `chartoken` FROM `session` WHERE `userid`=%d AND `expires` > NOW() AND `deleted`='n' ORDER BY `expires` DESC LIMIT 1",
							user.id.longValue());

			try {
				sessionConnection.connect();
				sessionConnection.executeQuery(getUserSessionQuery);

				if (sessionConnection.fetchNextRow()) {
					session = toSession(sessionConnection);
				}

				if (session != null) {
					String json = session.toString();
					asyncCache.put(memcacheKey, json);

					memcacheKey = getName() + ".id." + session.id;
					asyncCache.put(memcacheKey, json);

					memcacheKey = getName() + ".token." + session.token;
					asyncCache.put(memcacheKey, json);
				}
			} finally {
				if (sessionConnection != null) {
					sessionConnection.disconnect();
				}
			}
		} else {
			session = new Session();
			session.fromJson(jsonString);

			if (session.expires != null && new DateTime(session.expires, DateTimeZone.UTC).isBeforeNow()) {
				asyncCache.delete(memcacheKey);
				session = null;
			}
		}

		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.session.ISessionService#getTokenSession(java.lang.String)
	 */
	@Override
	public Session getTokenSession(String token) throws DataAccessException {
		Session session = null;
		String memcacheKey = getName() + ".token." + token;
		String jsonString = (String) syncCache.get(memcacheKey);

		if (jsonString == null) {
			IDatabaseService databaseService = DatabaseServiceProvider.provide();
			Connection sessionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

			String getUserSessionQuery = String
					.format("SELECT *, CAST(`token` AS CHAR) AS `chartoken` FROM `session` WHERE `token`=CAST('%s' AS BINARY) AND `expires` > NOW() AND `deleted`='n' ORDER BY `expires` DESC LIMIT 1",
							token);

			try {
				sessionConnection.connect();
				sessionConnection.executeQuery(getUserSessionQuery);

				if (sessionConnection.fetchNextRow()) {
					session = toSession(sessionConnection);
				}
			} finally {
				if (sessionConnection != null) {
					sessionConnection.disconnect();
				}
			}

			if (session != null) {
				String json = session.toString();
				asyncCache.put(memcacheKey, json);

				memcacheKey = getName() + ".userid." + session.user.id;
				asyncCache.put(memcacheKey, json);

				memcacheKey = getName() + ".id." + session.id;
				asyncCache.put(memcacheKey, json);
			}
		} else {
			session = new Session();
			session.fromJson(jsonString);
			
			if (session.expires != null && new DateTime(session.expires, DateTimeZone.UTC).isBeforeNow()) {
				asyncCache.delete(memcacheKey);
				session = null;
			}
		}

		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.session.ISessionService#extendSession(io.reflection.app.api.shared.datatypes.Session, java.lang.Long)
	 */
	@Override
	public Session extendSession(Session session, Long duration) throws DataAccessException {
		Session extendedSession = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection sessionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

		String extendSessionQuery = String.format("UPDATE `session` SET `expires`=date_add(now(), INTERVAL %d SECOND) WHERE `id`=%d AND `deleted`='n'",
				duration == null ? SESSION_SHORT_DURATION : duration.longValue(), session.id.longValue());

		try {
			sessionConnection.connect();
			sessionConnection.executeQuery(extendSessionQuery);

			if (sessionConnection.getAffectedRowCount() > 0) {
				String memcacheKey = getName() + ".id." + session.id;
				syncCache.delete(memcacheKey);

				extendedSession = getSession(session.id);
			} else {
				extendedSession = session;
			}
		} finally {
			if (sessionConnection != null) {
				sessionConnection.disconnect();
			}
		}

		return extendedSession;
	}

}