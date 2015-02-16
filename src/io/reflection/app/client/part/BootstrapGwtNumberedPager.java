//
//  BootstrapGwtDatePicker.java
//  storedata
//
//  Created by DanielG 
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author danielg
 * 
 */
public interface BootstrapGwtNumberedPager extends ClientBundle {
	public interface BootstrapGwtNumberedPagerStyles extends CssResource {

		@ClassName("selected")
		String selected();

		@ClassName("spaceApart")
		String spaceApart();
	}

	public static final BootstrapGwtNumberedPager INSTANCE = GWT.create(BootstrapGwtNumberedPager.class);

	@Source("res/bootstrapgwtnumberedpager.gss")
	BootstrapGwtNumberedPagerStyles styles();
}
