//  
//  NotificationService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.notification;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class NotificationService implements INotificationService {
	public String getName() {
		return ServiceType.ServiceTypeNotification.toString();
	}

	@Override
	public Notification getNotification(Long id) throws DataAccessException {
		Notification notification = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection notificationConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeNotification.toString());

		String getNotificationQuery = String.format("SELECT * FROM `notification` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			notificationConnection.connect();
			notificationConnection.executeQuery(getNotificationQuery);

			if (notificationConnection.fetchNextRow()) {
				notification = toNotification(notificationConnection);
			}
		} finally {
			if (notificationConnection != null) {
				notificationConnection.disconnect();
			}
		}
		return notification;
	}

	/**
	 * To notification
	 * 
	 * @param connection
	 * @return
	 */
	private Notification toNotification(Connection connection) throws DataAccessException {
		Notification notification = new Notification();
		notification.id = connection.getCurrentRowLong("id");
		notification.created = connection.getCurrentRowDateTime("created");
		notification.deleted = connection.getCurrentRowString("deleted");
		notification.longBody = connection.getCurrentRowString("longbody");
		return notification;
	}

	@Override
	public Notification addNotification(Notification notification) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Notification updateNotification(Notification notification) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteNotification(Notification notification) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}