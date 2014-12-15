//  
//  EventSubscriptionService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.eventsubscription;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.EventSubscription;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class EventSubscriptionService implements IEventSubscriptionService {
	public String getName() {
		return ServiceType.ServiceTypeEventSubscription.toString();
	}

	@Override
	public EventSubscription getEventSubscription(Long id) throws DataAccessException {
		EventSubscription eventSubscription = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection eventSubscriptionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEventSubscription.toString());

		String getEventSubscriptionQuery = String.format("SELECT * FROM `eventSubscription` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			eventSubscriptionConnection.connect();
			eventSubscriptionConnection.executeQuery(getEventSubscriptionQuery);

			if (eventSubscriptionConnection.fetchNextRow()) {
				eventSubscription = toEventSubscription(eventSubscriptionConnection);
			}
		} finally {
			if (eventSubscriptionConnection != null) {
				eventSubscriptionConnection.disconnect();
			}
		}
		return eventSubscription;
	}

	/**
	 * To eventSubscription
	 * 
	 * @param connection
	 * @return
	 */
	private EventSubscription toEventSubscription(Connection connection) throws DataAccessException {
		EventSubscription eventSubscription = new EventSubscription();
		eventSubscription.id(connection.getCurrentRowLong("id")).created(connection.getCurrentRowDateTime("created"))
				.deleted(connection.getCurrentRowString("deleted"));
		eventSubscription.eavesDropping((User) new User().id(connection.getCurrentRowLong("eavesdroppingid")))
				.email(EventPriorityType.fromString(connection.getCurrentRowString("email")))
				.event((Event) new Event().id(connection.getCurrentRowLong("eventid")))
				.notificationCenter(EventPriorityType.fromString(connection.getCurrentRowString("notificationcenter")))
				.push(EventPriorityType.fromString(connection.getCurrentRowString("push")))
				.text(EventPriorityType.fromString(connection.getCurrentRowString("text"))).user((User) new User().id(connection.getCurrentRowLong("userid")));
		return eventSubscription;
	}

	@Override
	public EventSubscription addEventSubscription(EventSubscription eventSubscription) throws DataAccessException {
		throw new UnsupportedOperationException("addEventSubscription");
	}

	@Override
	public EventSubscription updateEventSubscription(EventSubscription eventSubscription) throws DataAccessException {
		throw new UnsupportedOperationException("updateEventSubscription");
	}

	@Override
	public void deleteEventSubscription(EventSubscription eventSubscription) throws DataAccessException {
		throw new UnsupportedOperationException("deleteEventSubscription");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.eventsubscription.IEventSubscriptionService#getUserEventEventSubscription(io.reflection.app.datatypes.shared.Event,
	 * io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public EventSubscription getUserEventEventSubscription(Event event, User user) throws DataAccessException {
		throw new UnsupportedOperationException("getUserEventEventSubscription");
	}

}