//  
//  EventService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.event;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class EventService implements IEventService {
	public String getName() {
		return ServiceType.ServiceTypeEvent.toString();
	}

	@Override
	public Event getEvent(Long id) throws DataAccessException {
		Event event = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection eventConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEvent.toString());

		String getEventQuery = String.format("SELECT * FROM `event` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			eventConnection.connect();
			eventConnection.executeQuery(getEventQuery);

			if (eventConnection.fetchNextRow()) {
				event = toEvent(eventConnection);
			}
		} finally {
			if (eventConnection != null) {
				eventConnection.disconnect();
			}
		}
		return event;
	}

	/**
	 * To event
	 * 
	 * @param connection
	 * @return
	 */
	private Event toEvent(Connection connection) throws DataAccessException {
		Event event = new Event();
		event.id = connection.getCurrentRowLong("id");
		return event;
	}

	@Override
	public Event addEvent(Event event) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Event updateEvent(Event event) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteEvent(Event event) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}