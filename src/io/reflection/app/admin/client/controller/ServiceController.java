//
//  ServiceController.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import com.google.gwt.user.client.Window;

/**
 * @author billy1380
 * 
 */
public interface ServiceController {

	public static final String HOST = Window.Location.getHost();

	public static final String CORE_END_POINT = "//" + HOST + "/core";

	public static final String ADMIN_END_POINT = "//" + HOST + "/admin";

	public static final String ACCESS_CODE = "b72b4e32-1062-4cc7-bc6b-52498ee10f09";

	public static final int STEP_VALUE = 25;
	
	public static final int SHORT_STEP_VALUE = 10;
	
	public static final Long SHORT_STEP = Long.valueOf(SHORT_STEP_VALUE);

	public static final Long STEP = Long.valueOf(STEP_VALUE);

}
