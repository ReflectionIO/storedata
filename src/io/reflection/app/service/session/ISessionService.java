//  
//  ISessionService.java
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

import com.spacehopperstudios.service.IService;

public interface ISessionService extends IService {
	/**
	 * 20 minutes in seconds
	 */
	public static final long SESSION_SHORT_DURATION = 60 * 20;

	/**
	 * Approximately 30 days in seconds
	 */
	public static final long SESSION_LONG_DURATION = 60 * 60 * 24 * 30;

	/**
	 * @param id
	 * @return
	 */
	public Session getSession(Long id) throws DataAccessException;

	/**
	 * @param session
	 * @return
	 */
	public Session addSession(Session session) throws DataAccessException;

	/**
	 * @param session
	 * @return
	 */
	public Session updateSession(Session session) throws DataAccessException;

	/**
	 * @param session
	 */
	public void deleteSession(Session session) throws DataAccessException;

	/**
	 * Create user session
	 * 
	 * @param user
	 * @param longTerm
	 * @return
	 */
	public Session createUserSession(User user, Boolean longTerm) throws DataAccessException;

	/**
	 * Get user session
	 * 
	 * @param user
	 * @return
	 */
	public Session getUserSession(User user) throws DataAccessException;

	/**
	 * @param token
	 * @return
	 */
	public Session getTokenSession(String token) throws DataAccessException;

	/**
	 * @param session
	 * @param duration
	 * @return
	 */
	public Session extendSession(Session session, Long sessionShortDuration) throws DataAccessException;

}