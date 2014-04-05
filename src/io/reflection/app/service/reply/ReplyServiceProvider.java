//  
//  ReplyServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.reply;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class ReplyServiceProvider {

	/**
	 * @return
	 */
	public static IReplyService provide() {
		IReplyService replyService = null;

		if ((replyService = (IReplyService) ServiceDiscovery.getService(ServiceType.ServiceTypeReply.toString())) == null) {
			replyService = ReplyServiceFactory.createNewReplyService();
			ServiceDiscovery.registerService(replyService);
		}

		return replyService;
	}

}