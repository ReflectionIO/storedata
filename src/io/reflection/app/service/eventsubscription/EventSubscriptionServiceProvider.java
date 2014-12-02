//  
//  EventSubscriptionServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.eventsubscription;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class EventSubscriptionServiceProvider {

	/**
	 * @return
	 */
	public static IEventSubscriptionService provide() {
		IEventSubscriptionService eventSubscriptionService = null;

		if ((eventSubscriptionService = (IEventSubscriptionService) ServiceDiscovery.getService(ServiceType.ServiceTypeEventSubscription.toString())) == null) {
			eventSubscriptionService = EventSubscriptionServiceFactory.createNewEventSubscriptionService();
			ServiceDiscovery.registerService(eventSubscriptionService);
		}

		return eventSubscriptionService;
	}

}