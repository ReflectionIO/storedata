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
public interface BootstrapGwtDisableAnchor extends ClientBundle {
	public interface BootstrapGwtDisableAnchorStyles extends CssResource {

		@ClassName("disabled")
		String disabled();
	}

	public static final BootstrapGwtDisableAnchor INSTANCE = GWT.create(BootstrapGwtDisableAnchor.class);

	@Source("res/bootstrapgwtdisableanchor.gss")
	BootstrapGwtDisableAnchorStyles styles();
}
