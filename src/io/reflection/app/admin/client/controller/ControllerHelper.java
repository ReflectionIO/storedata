//
//  ControllerHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import com.google.gwt.user.client.Window;

/**
 * @author billy1380
 * 
 */
public class ControllerHelper {

	public static final String HOST = Window.Location.getHost();

	public static final String CORE_END_POINT = "//" + HOST + "/core";

	public static final String ADMIN_END_POINT = "//" + HOST + "/admin";

	public static final String ACCESS_CODE = "b72b4e32-1062-4cc7-bc6b-52498ee10f09";

}
