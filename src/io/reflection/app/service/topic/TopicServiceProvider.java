//  
//  TopicServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.topic;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class TopicServiceProvider {

	/**
	 * @return
	 */
	public static ITopicService provide() {
		ITopicService topicService = null;

		if ((topicService = (ITopicService) ServiceDiscovery.getService(ServiceType.ServiceTypeTopic.toString())) == null) {
			topicService = TopicServiceFactory.createNewTopicService();
			ServiceDiscovery.registerService(topicService);
		}

		return topicService;
	}

}