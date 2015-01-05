//  
//  EventSubscriptionServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on December 2, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.eventsubscription;

final class EventSubscriptionServiceFactory {

	/**
	 * @return
	 */
	public static IEventSubscriptionService createNewEventSubscriptionService() {
		return new EventSubscriptionService();
	}

}