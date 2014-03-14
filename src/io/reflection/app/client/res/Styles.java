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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author billy1380
 * 
 */
public interface Styles extends ClientBundle {
	public interface ReflectionStyles extends CssResource {
		String highlightRange();

		String highlightRangeBoundaries();

		String footerFacebook();

		String footerLinkedin();

		String footerTwitter();

		String footerLogo();

		String footerUpArrow();

		String footerDownArrow();
	}

	public static final Styles INSTANCE = GWT.create(Styles.class);

	@Source("reflection.css")
	ReflectionStyles reflection();

	@Source("footersprite.png")
	@ImageOptions(repeatStyle = RepeatStyle.None)
	ImageResource footerSprite();

}
