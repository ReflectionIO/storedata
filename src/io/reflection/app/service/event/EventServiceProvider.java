//  
//  EventServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.event;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class EventServiceProvider {

	/**
	 * @return
	 */
	public static IEventService provide() {
		IEventService eventService = null;

		if ((eventService = (IEventService) ServiceDiscovery.getService(ServiceType.ServiceTypeEvent.toString())) == null) {
			eventService = EventServiceFactory.createNewEventService();
			ServiceDiscovery.registerService(eventService);
		}

		return eventService;
	}

}