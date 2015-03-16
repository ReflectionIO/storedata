//
//  Images.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 17 Feb 2014.
//  Copyright © 2014 Reflection.io. All rights reserved.
//
package io.reflection.app.client.res.charts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author billy1380
 * 
 */
public interface Images extends ClientBundle {
	public static final Images INSTANCE = GWT.create(Images.class);

	@Source("purplecircle.png")
	ImageResource purpleCirle();

	@Source("pinkcircle.png")
	ImageResource pinkCirle();

	@Source("greencircle.png")
	ImageResource greenCirle();

	@Source("orangecircle.png")
	ImageResource orangeCirle();

}
