//
//  ReplyController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

/**
 * @author billy1380
 * 
 */
public class ReplyController implements ServiceConstants {

	private static ReplyController one = null;

	public static ReplyController get() {
		if (one == null) {
			one = new ReplyController();
		}

		return one;
	}

//	private void fetchReplies() {
//
//	}

	/**
	 * @param topicId
	 */
	public void getReplies(Long topicId) {

	}

}
