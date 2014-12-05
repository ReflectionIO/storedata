//  
//  IEventService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.event;

import java.util.List;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Event;

import com.spacehopperstudios.service.IService;

public interface IEventService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public Event getEvent(Long id) throws DataAccessException;

	/**
	 * @param event
	 * @return
	 * @throws DataAccessException
	 */
	public Event addEvent(Event event) throws DataAccessException;

	/**
	 * @param event
	 * @return
	 * @throws DataAccessException
	 */
	public Event updateEvent(Event event) throws DataAccessException;

	/**
	 * @param event
	 * @throws DataAccessException
	 */
	public void deleteEvent(Event event) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Event> getEvents(Pager pager) throws DataAccessException;

	/**
	 * @return
	 * @throws DataAccessException
	 */
	public Long getEventsCount() throws DataAccessException;

	/**
	 * @param newUserEventKey
	 * @return
	 * @throws DataAccessException
	 */
	public Event getCodeEvent(String code) throws DataAccessException;

}