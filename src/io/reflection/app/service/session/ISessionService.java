//  
//  ISessionService.java
//  reflection.io
//
//  Created by William Shakour on November 25, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.session;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.User;

import com.spacehopperstudios.service.IService;

public interface ISessionService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Session getSession(Long id);

	/**
	 * @param session
	 * @return
	 */
	public Session addSession(Session session);

	/**
	 * @param session
	 * @return
	 */
	public Session updateSession(Session session);

	/**
	 * @param session
	 */
	public void deleteSession(Session session);

	/**
	 * Create user session
	 * @param user
	 * @return
	 */
    public Session createUserSession (User user);

    /**
     * Get user session
     * @param user
     * @return
     */
    public Session getUserSession (User user);

	/**
	 * @param token
	 * @return
	 */
	public Session getTokenSession(String token);

}