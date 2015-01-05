//
//  BootstrapGwtSuggestBox.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Dec 2014.
////  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author billy1380
 * 
 */
public interface BootstrapGwtSuggestBox extends ClientBundle {
	public static final BootstrapGwtSuggestBox INSTANCE = GWT.create(BootstrapGwtSuggestBox.class);

	public interface BootstrapGwtSuggestBoxStyle extends CssResource {};
	
	@Source("res/bootstrapgwtsuggestbox.css")
	BootstrapGwtSuggestBoxStyle styles();
};
