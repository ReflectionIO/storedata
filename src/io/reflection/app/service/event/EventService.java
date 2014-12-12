//  
//  EventService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.event;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.EventTypeType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.List;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.event.IEventService#getCodeEvent(java.lang.String)
	 */
	@Override
	public Event getCodeEvent(String code) throws DataAccessException {
		Event event = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection eventConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEvent.toString());

		String getEventQuery = String.format("SELECT * FROM `event` WHERE `deleted`='n' AND `code`='%s' LIMIT 1", addslashes(code));
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
		event.id(connection.getCurrentRowLong("id")).created(connection.getCurrentRowDateTime("created")).deleted(connection.getCurrentRowString("deleted"));
		event.code(stripslashes(connection.getCurrentRowString("code"))).description(stripslashes(connection.getCurrentRowString("description")))
				.name(stripslashes(connection.getCurrentRowString("name"))).longBody(stripslashes(connection.getCurrentRowString("longbody")))
				.shortBody(stripslashes(connection.getCurrentRowString("shortbody"))).subject(stripslashes(connection.getCurrentRowString("subject")))
				.priority(EventPriorityType.fromString(connection.getCurrentRowString("priority")))
				.type(EventTypeType.fromString(connection.getCurrentRowString("type")));
		return event;
	}

	@Override
	public Event addEvent(Event event) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Event updateEvent(Event event) throws DataAccessException {
		Event updatedEvent = null;

		final String updateEventQuery = String
				.format("UPDATE `event` SET `code`='%s', `name`='%s', `description`='%s', `subject`='%s', `shortbody`='%s', `longbody`='%s',`priority`='%s',`type`='%s'  WHERE `id`=%d AND `deleted`='n'",
						addslashes(event.code), addslashes(event.name), addslashes(event.description), addslashes(event.subject), addslashes(event.shortBody),
						addslashes(event.longBody), event.priority.toString(), event.type.toString(), event.id.longValue());

		Connection eventConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeEvent.toString());

		try {
			eventConnection.connect();
			eventConnection.executeQuery(updateEventQuery);

			if (eventConnection.getAffectedRowCount() > 0) {
				updatedEvent = getEvent(event.id);
			}
		} finally {
			if (eventConnection != null) {
				eventConnection.disconnect();
			}
		}

		return updatedEvent;
	}

	@Override
	public void deleteEvent(Event event) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.event.IEventService#getEvents(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Event> getEvents(Pager pager) throws DataAccessException {
		List<Event> events = new ArrayList<Event>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection eventConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEvent.toString());

		String getEventsQuery = "SELECT * FROM `event` WHERE `deleted`='n'";

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null && ("code".equals(pager.sortBy) || "name".equals(pager.sortBy))) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
				case SortDirectionTypeAscending:
					sortDirectionQuery = "ASC";
					break;
				default:
					break;
				}
			}

			getEventsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getEventsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getEventsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		try {
			eventConnection.connect();
			eventConnection.executeQuery(getEventsQuery);

			while (eventConnection.fetchNextRow()) {
				Event event = this.toEvent(eventConnection);

				if (event != null) {
					events.add(event);
				}
			}
		} finally {
			if (eventConnection != null) {
				eventConnection.disconnect();
			}
		}

		return events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.event.IEventService#getEventsCount()
	 */
	@Override
	public Long getEventsCount() throws DataAccessException {
		throw new UnsupportedOperationException("getEventsCount");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.event.IEventService#searchEvents(java.lang.String, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Event> searchEvents(String mask, Pager pager) throws DataAccessException {
		List<Event> events = new ArrayList<Event>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection eventConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEvent.toString());

		String searchEventsQuery = String.format("SELECT * FROM `event` WHERE `deleted`='n' AND (`code` LIKE '%%%1$s%%' OR `name` LIKE '%%%1$s%%')",
				addslashes(mask));

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null
					&& ("code".equals(pager.sortBy) || "name".equals(pager.sortBy) || "type".equals(pager.sortBy) || "priority".equals(pager.sortBy))) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
				case SortDirectionTypeAscending:
					sortDirectionQuery = "ASC";
					break;
				default:
					break;
				}
			}

			searchEventsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			searchEventsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			searchEventsQuery += String.format(" LIMIT %d", pager.count);
		}

		try {
			eventConnection.connect();
			eventConnection.executeQuery(searchEventsQuery);

			while (eventConnection.fetchNextRow()) {
				Event event = this.toEvent(eventConnection);

				if (event != null) {
					events.add(event);
				}
			}
		} finally {
			if (eventConnection != null) {
				eventConnection.disconnect();
			}
		}

		return events;
	}

}