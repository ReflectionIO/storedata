//
//  BootstrapGwtDatePicker.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtDatePicker extends ClientBundle {
	public interface BootstrapGwtDatePickerStyles extends CssResource {}

	public static final BootstrapGwtDatePicker INSTANCE = GWT.create(BootstrapGwtDatePicker.class);

	@Source("css/bootstrapgwtdatepicker.css")
	BootstrapGwtDatePickerStyles styles();
}
