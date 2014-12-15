//  
//  NotificationService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.notification;

import static com.spacehopperstudios.utility.StringUtils.stripslashes;

import java.util.ArrayList;
import java.util.List;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.EventSubscription;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationStatusType;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.User;
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
		notification.id(connection.getCurrentRowLong("id")).created(connection.getCurrentRowDateTime("created"))
				.deleted(connection.getCurrentRowString("deleted"));
		notification.cause((EventSubscription) new EventSubscription().id(connection.getCurrentRowLong("causeid")))
				.from(connection.getCurrentRowString("from")).body(stripslashes(connection.getCurrentRowString("body")))
				.status(NotificationStatusType.fromString(connection.getCurrentRowString("status")))
				.subject(stripslashes(connection.getCurrentRowString("subject"))).type(NotificationTypeType.fromString(connection.getCurrentRowString("type")));
		return notification;
	}

	@Override
	public Notification addNotification(Notification notification) throws DataAccessException {
		Notification addedNotification = null;

		if (notification.type == null) {
			notification.type = NotificationTypeType.NotificationTypeTypeInternal;
		}

		if (notification.status == null && notification.type != NotificationTypeType.NotificationTypeTypeInternal) {
			notification.status = NotificationStatusType.NotificationStatusTypeSending;
		} else {
			notification.status = NotificationStatusType.NotificationStatusTypeSent;
		}

		if (notification.priority == null) {
			notification.priority = EventPriorityType.EventPriorityTypeNormal;
		}

		String addNotificationQuery = String
				.format("INSERT INTO `notification` (`causeid`,`eventid`,`userid`,`from`,`subject`,`body`,`status`,`type`,`priority`) values (%s,%s,%d,'%s','%s','%s','%s','%s','%s')",
						notification.cause == null ? "NULL" : notification.cause.id.toString(),
						notification.event == null ? "NULL" : notification.event.id.toString(), notification.user.id.longValue(),
						stripslashes(notification.from), stripslashes(notification.subject), stripslashes(notification.body), notification.status.toString(),
						notification.type.toString(), notification.priority.toString());

		Connection notificationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeNotification.toString());

		try {
			notificationConnection.connect();
			notificationConnection.executeQuery(addNotificationQuery);

			if (notificationConnection.getAffectedRowCount() > 0) {
				notification.id = Long.valueOf(notificationConnection.getInsertedId());

				addedNotification = getNotification(notification.id);
			}
		} finally {
			if (notificationConnection != null) {
				notificationConnection.disconnect();
			}
		}

		return addedNotification;
	}

	@Override
	public Notification updateNotification(Notification notification) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteNotification(Notification notification) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.notification.INotificationService#getUserNotifications(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.datatypes.shared.NotificationTypeType, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Notification> getUserNotifications(User user, NotificationTypeType type, Pager pager) throws DataAccessException {
		List<Notification> notifications = new ArrayList<Notification>();

		String getUserNotificationsQuery = String.format("SELECT * FROM `notification` WHERE %s `deleted`='n' AND `userid`=%d ORDER BY `%s` %s LIMIT %d",
				type == null ? "" : "`type`='" + type.toString() + "' AND", user.id.longValue(), pager.sortBy == null ? "id" : stripslashes(pager.sortBy),
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start == null ? Pager.DEFAULT_START.longValue()
						: pager.start.longValue(), pager.count == null ? Pager.DEFAULT_COUNT.longValue() : pager.count.longValue());

		Connection notificationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeNotification.toString());
		try {
			notificationConnection.connect();
			notificationConnection.executeQuery(getUserNotificationsQuery);

			while (notificationConnection.fetchNextRow()) {
				Notification notification = toNotification(notificationConnection);
				if (notification != null) {
					notifications.add(notification);
				}
			}
		} finally {
			if (notificationConnection != null) {
				notificationConnection.disconnect();
			}
		}

		return notifications;
	}
}