//  
//  NotificationServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.notification;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class NotificationServiceProvider {

	/**
	 * @return
	 */
	public static INotificationService provide() {
		INotificationService notificationService = null;

		if ((notificationService = (INotificationService) ServiceDiscovery.getService(ServiceType.ServiceTypeNotification.toString())) == null) {
			notificationService = NotificationServiceFactory.createNewNotificationService();
			ServiceDiscovery.registerService(notificationService);
		}

		return notificationService;
	}

}