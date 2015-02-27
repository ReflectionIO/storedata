//  
//  NotificationService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.notification;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.EventSubscription;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationStatusType;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.persistentcounter.CounterServiceFactory;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.eventsubscription.EventSubscriptionServiceProvider;
import io.reflection.app.shared.util.FormattingHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.markdown4j.server.MarkdownProcessor;

final class NotificationService implements INotificationService {

	private static final String COUNTER_USER_NOTIFICATIONS = "notification.user.%d";
	private static final String COUNTER_USER_NOTIFICATIONS_UNREAD = "notification.user.%d.unread";
	private static final String COUNTER_USER_NOTIFICATIONS_UNREAD_CRITICAL = "notification.user.%d.unread.critical";

	private static final Logger LOG = Logger.getLogger(NotificationService.class.getName());

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
		notification.from(stripslashes(connection.getCurrentRowString("from"))).body(stripslashes(connection.getCurrentRowString("body")))
				.status(NotificationStatusType.fromString(connection.getCurrentRowString("status")))
				.subject(stripslashes(connection.getCurrentRowString("subject"))).type(NotificationTypeType.fromString(connection.getCurrentRowString("type")))
				.priority(EventPriorityType.fromString(connection.getCurrentRowString("priority")));

		Long causeId = connection.getCurrentRowLong("causeid");
		if (causeId != null) {
			(notification.cause = new EventSubscription()).id(causeId);
		}

		Long eventId = connection.getCurrentRowLong("eventid");
		if (eventId != null) {
			(notification.event = new Event()).id(eventId);
		}

		Long userId = connection.getCurrentRowLong("userid");
		if (userId != null) {
			(notification.user = new User()).id(userId);
		}

		return notification;
	}

	@Override
	public Notification addNotification(Notification notification) throws DataAccessException {
		Notification addedNotification = null;

		if (notification.cause != null) {
			if (notification.cause.user == null || notification.cause.event == null) {
				notification.cause = EventSubscriptionServiceProvider.provide().getEventSubscription(notification.cause.id);

				notification.event = notification.cause.event;
				notification.user = notification.cause.user;
			}
		}

		if (notification.status == null && notification.type != NotificationTypeType.NotificationTypeTypeInternal) {
			notification.status = NotificationStatusType.NotificationStatusTypeSending;
		} else {
			notification.status = NotificationStatusType.NotificationStatusTypeSent;
		}

		if (notification.priority == null) {
			if (notification.event == null) {
				notification.priority = EventPriorityType.EventPriorityTypeNormal;
			} else {
				if (notification.event.priority == null) {
					notification.event = EventServiceProvider.provide().getEvent(notification.event.id);
				}

				notification.priority = notification.event.priority;
			}
		}

		if (notification.type == null) {
			if (notification.priority == null) {
				notification.type = NotificationTypeType.NotificationTypeTypeInternal;
			} else {
				switch (notification.priority) {
				case EventPriorityTypeCritical:
				case EventPriorityTypeHigh:
				case EventPriorityTypeNormal:
					notification.type = NotificationTypeType.NotificationTypeTypeEmail;
					break;
				default:
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					break;
				}
			}
		}

		String addNotificationQuery = String
				.format("INSERT INTO `notification` (`causeid`,`eventid`,`userid`,`from`,`subject`,`body`,`status`,`type`,`priority`) values (%s,%s,%d,'%s','%s','%s','%s','%s','%s')",
						notification.cause == null ? "NULL" : notification.cause.id.toString(),
						notification.event == null ? "NULL" : notification.event.id.toString(), notification.user.id.longValue(),
						addslashes(notification.from), addslashes(notification.subject), addslashes(notification.body), notification.status.toString(),
						notification.type.toString(), notification.priority.toString());

		Connection notificationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeNotification.toString());

		try {
			notificationConnection.connect();
			notificationConnection.executeQuery(addNotificationQuery);

			if (notificationConnection.getAffectedRowCount() > 0) {
				notification.id = Long.valueOf(notificationConnection.getInsertedId());

				if (notification.id != null) {
					addedNotification = getNotification(notification.id);

					// replace with more complete objects
					addedNotification.user(notification.user).event(notification.event).cause(notification.cause);

					if (addedNotification.type == NotificationTypeType.NotificationTypeTypeEmail) {
						String markdownBody = addedNotification.body;
						String body = markdownBody;

						try {
							body = (new MarkdownProcessor()).process(markdownBody);
						} catch (IOException e) {
							if (LOG.isLoggable(GaeLevel.WARNING)) {
								LOG.log(GaeLevel.WARNING, "Failed to process markdown [" + markdownBody + "]", e);
							}
						}

						if (NotificationHelper.sendEmail(addedNotification.from, addedNotification.user.username,
								FormattingHelper.getUserName(addedNotification.user), addedNotification.subject, body, !markdownBody.equals(body))) {

							addedNotification.status = NotificationStatusType.NotificationStatusTypeSent;
							addedNotification = updateNotification(addedNotification);

							// replace with more complete objects
							addedNotification.user(notification.user).event(notification.event).cause(notification.cause);
						} else {
							LOG.severe(String.format("Failed to send email for notification [%d]", addedNotification.id.longValue()));
						}
					} else if (addedNotification.type == NotificationTypeType.NotificationTypeTypeInternal) {
						String userCounter = String.format(COUNTER_USER_NOTIFICATIONS, addedNotification.user.id.longValue());
						CounterServiceFactory.getCounterService().increment(userCounter);

						String userUnReadCounter = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD, addedNotification.user.id.longValue());
						CounterServiceFactory.getCounterService().increment(userUnReadCounter);

						if (addedNotification.priority == EventPriorityType.EventPriorityTypeCritical) {
							String userUnReadCriticalCounter = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD_CRITICAL, addedNotification.user.id.longValue());
							CounterServiceFactory.getCounterService().increment(userUnReadCriticalCounter);
						}
					}
				}
			}
		} finally {
			if (notificationConnection != null) {
				notificationConnection.disconnect();
			}
		}

		return addedNotification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.notification.INotificationService#updateNotification(io.reflection.app.datatypes.shared.Notification)
	 */
	@Override
	public Notification updateNotification(Notification notification) throws DataAccessException {
		return updateNotification(null, notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.notification.INotificationService#updateNotification(io.reflection.app.datatypes.shared.Notification,
	 * io.reflection.app.datatypes.shared.Notification)
	 */
	@Override
	public Notification updateNotification(Notification existing, Notification toUpdate) throws DataAccessException {
		Notification updatedNotification = null;

		final String updateNotificationQuery = String
				.format("UPDATE `notification` SET `causeid`=%s,`eventid`=%s,`userid`=%s,`from`='%s',`subject`='%s',`body`='%s',`status`='%s',`type`='%s' WHERE `deleted`='n' AND `id`=%d",
						toUpdate.cause == null ? "NULL" : toUpdate.cause.id.toString(), toUpdate.event == null ? "NULL" : toUpdate.event.id.toString(),
						toUpdate.user == null ? "NULL" : toUpdate.user.id.toString(), addslashes(toUpdate.from), addslashes(toUpdate.subject),
						addslashes(toUpdate.body), toUpdate.status.toString(), toUpdate.type.toString(), toUpdate.id.longValue());

		Connection notificationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeNotification.toString());

		try {
			notificationConnection.connect();
			notificationConnection.executeQuery(updateNotificationQuery);

			if (notificationConnection.getAffectedRowCount() > 0) {
				updatedNotification = getNotification(toUpdate.id);

				if (updatedNotification.type == NotificationTypeType.NotificationTypeTypeInternal) {
					if (existing != null && existing.status != NotificationStatusType.NotificationStatusTypeRead
							&& updatedNotification.status == NotificationStatusType.NotificationStatusTypeRead) {
						String userUnReadCounter = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD, toUpdate.user.id.longValue());
						CounterServiceFactory.getCounterService().increment(userUnReadCounter, Integer.valueOf(-1));

						if (updatedNotification.priority == EventPriorityType.EventPriorityTypeCritical) {
							String userUnReadCriticalCounter = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD_CRITICAL, toUpdate.user.id.longValue());
							CounterServiceFactory.getCounterService().increment(userUnReadCriticalCounter, Integer.valueOf(-1));
						}
					}
				}
			} else {
				updatedNotification = toUpdate;
			}
		} finally {
			if (notificationConnection != null) {
				notificationConnection.disconnect();
			}
		}

		return updatedNotification;
	}

	@Override
	public void deleteNotification(Notification notification) throws DataAccessException {
		if (notification.status == null || notification.type == null || notification.priority == null || notification.user == null) {
			notification = getNotification(notification.id);
		}

		String deleteNotificationQuery = String.format("UPDATE `notification` SET `deleted`='y' WHERE `id`=%d", notification.id.longValue());
		Connection notificationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeNotification.toString());
		try {
			notificationConnection.connect();
			notificationConnection.executeQuery(deleteNotificationQuery);

			if (notificationConnection.getAffectedRowCount() > 0) {
				if (notification.type == NotificationTypeType.NotificationTypeTypeInternal) {
					if (notification.status != NotificationStatusType.NotificationStatusTypeRead) {
						String userUnReadCounter = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD, notification.user.id.longValue());
						CounterServiceFactory.getCounterService().increment(userUnReadCounter, Integer.valueOf(-1));

						if (notification.priority == EventPriorityType.EventPriorityTypeCritical) {
							String userUnReadCriticalCounter = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD_CRITICAL, notification.user.id.longValue());
							CounterServiceFactory.getCounterService().increment(userUnReadCriticalCounter, Integer.valueOf(-1));
						}
					}
				}
			}
		} finally {
			if (notificationConnection != null) {
				notificationConnection.disconnect();
			}
		}
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

		String getUserNotificationsQuery = String.format("SELECT * FROM `notification` WHERE %s `deleted`='n' AND `userid`=%d ORDER BY `%s` %s LIMIT %d, %d",
				type == null ? "" : "`type`='" + type.toString() + "' AND", user.id.longValue(), pager.sortBy == null ? "id" : addslashes(pager.sortBy),
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.notification.INotificationService#getUserNotificationsCount(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.datatypes.shared.NotificationTypeType, io.reflection.app.datatypes.shared.EventPriorityType, java.lang.Boolean)
	 */
	@Override
	public Long getUserNotificationsCount(User user, NotificationTypeType type, EventPriorityType priority, Boolean unread) throws DataAccessException {
		Long userNotificationCount = Long.valueOf(0);

		if (type == null) {
			type = NotificationTypeType.NotificationTypeTypeInternal;
		}

		if (type != NotificationTypeType.NotificationTypeTypeInternal)
			throw new UnsupportedOperationException("getUserNotificationsCount is only valid for messages of type ["
					+ NotificationTypeType.NotificationTypeTypeInternal + "]");

		if (priority != null && priority != EventPriorityType.EventPriorityTypeCritical)
			throw new UnsupportedOperationException("getUserNotificationsCount is only valid for all messages or messages of priority ["
					+ EventPriorityType.EventPriorityTypeCritical + "]");

		if (unread == null) {
			unread = Boolean.FALSE;
		}

		String counterName = null;

		if (unread.booleanValue() && priority == null) {
			counterName = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD, user.id.longValue());
		} else if (unread.booleanValue() && priority == EventPriorityType.EventPriorityTypeCritical) {
			counterName = String.format(COUNTER_USER_NOTIFICATIONS_UNREAD_CRITICAL, user.id.longValue());
		} else if (!unread.booleanValue() && priority == null) {
			counterName = String.format(COUNTER_USER_NOTIFICATIONS, user.id.longValue());
		}

		if (counterName == null) throw new UnsupportedOperationException("getUserNotificationsCount is not supported for parameters");

		userNotificationCount = CounterServiceFactory.getCounterService().getCount(counterName);

		return userNotificationCount;
	}
}