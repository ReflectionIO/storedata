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

}