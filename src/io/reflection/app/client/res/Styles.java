//
//  Styles.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author billy1380
 *
 */
public interface Styles extends ClientBundle {
	public interface ReflectionStyles extends CssResource {
		String highlightRange();
		String highlightRangeBoundaries();
	}

	public static final Styles INSTANCE = GWT.create(Styles.class);

	@Source("reflection.css")
	ReflectionStyles reflection();
	
}
