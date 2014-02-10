//
//  Images.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author billy1380
 * 
 */
public interface Images extends ClientBundle {
	public static final Images INSTANCE = GWT.create(Images.class);

	@Source("logo.png")
	ImageResource logo();

	@Source("spinner.gif")
	ImageResource spinner();
	
	@Source("spinnerinfo.gif")
	ImageResource spinnerInfo();
	
	@Source("spinnertableodd.gif")
	ImageResource spinnerTableOdd();
	
	@Source("spinnertableeven.gif")
	ImageResource spinnerTableEven();
	
	@Source("alarmclock.png")
	ImageResource alarmClock();
	
	@Source("clipboard.png")
	ImageResource clipboard();
	
	@Source("tick.png")
	ImageResource tick();
}
