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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

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
	 * 
	 * @param existing
	 * @param toUpdate
	 * @return
	 * @throws DataAccessException
	 */
	public Notification updateNotification(Notification existing, Notification toUpdate) throws DataAccessException;

	/**
	 * 
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

	/**
	 * @param user
	 * @param type
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Notification> getUserNotifications(User user, NotificationTypeType type, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param type
	 * @param priority
	 * @param unread
	 * @return
	 * @throws DataAccessException
	 */
	public Long getUserNotificationsCount(User user, NotificationTypeType type, EventPriorityType priority, Boolean unread) throws DataAccessException;
}