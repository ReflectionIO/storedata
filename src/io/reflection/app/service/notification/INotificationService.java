//  
//  INotificationService.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.notification;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Notification;

import com.spacehopperstudios.service.IService;

public interface INotificationService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public Notification getNotification(Long id) throws DataAccessException;

	/**
	 * @param notification
	 * @return
	 * @throws DataAccessException
	 */
	public Notification addNotification(Notification notification) throws DataAccessException;

	/**
	 * @param notification
	 * @return
	 * @throws DataAccessException
	 */
	public Notification updateNotification(Notification notification) throws DataAccessException;

	/**
	 * @param notification
	 * @throws DataAccessException
	 */
	public void deleteNotification(Notification notification) throws DataAccessException;

}