//
//  EventController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * @author billy1380
 * 
 */
public class EventController {

	private static EventBus mBus;

	public static EventBus get() {
		if (mBus == null) {
			mBus = new SimpleEventBus();
		}

		return mBus;
	}

}
