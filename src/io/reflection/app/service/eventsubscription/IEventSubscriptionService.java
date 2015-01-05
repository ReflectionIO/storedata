//  
//  IEventSubscriptionService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.eventsubscription;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventSubscription;
import io.reflection.app.datatypes.shared.User;

import com.spacehopperstudios.service.IService;

public interface IEventSubscriptionService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public EventSubscription getEventSubscription(Long id) throws DataAccessException;

	/**
	 * @param eventSubscription
	 * @return
	 * @throws DataAccessException
	 */
	public EventSubscription addEventSubscription(EventSubscription eventSubscription) throws DataAccessException;

	/**
	 * @param eventSubscription
	 * @return
	 * @throws DataAccessException
	 */
	public EventSubscription updateEventSubscription(EventSubscription eventSubscription) throws DataAccessException;

	/**
	 * @param eventSubscription
	 * @throws DataAccessException
	 */
	public void deleteEventSubscription(EventSubscription eventSubscription) throws DataAccessException;

	/**
	 * @param event
	 * @param user
	 * @return
	 * @throws DataAccessException
	 */
	public EventSubscription getUserEventEventSubscription(Event event, User user) throws DataAccessException;

}