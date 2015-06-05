//
//  Styles.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 17 Feb 2014.
//  Copyright © 2014 Reflection.io. All rights reserved.
//
package io.reflection.app.client.res.flags;

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
public interface StylesFlags extends ClientBundle {
	public static final StylesFlags INSTANCE = GWT.create(StylesFlags.class);

	interface FlagStyles extends CssResource {
		String cn();
		String de();
		String fr();
		String gb();
		String us();
		String jp();
		String it();
	}
	
	@Source("flags.png")
	@ImageOptions(repeatStyle = RepeatStyle.None)
	ImageResource spriteSheet();
	
	
	@Source("flags.gss")
	FlagStyles flags();

}
