//
//  Preloader.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 17 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author stefanocapuzzi
 * 
 */
public class Preloader extends PopupPanel {

	HTMLPanel preloaderWidget = new HTMLPanel("");

	private static Preloader one = null;

	public static Preloader get() {
		if (one == null) {
			one = new Preloader();
		}

		return one;
	}

	public Preloader() {
		this.hide(true);
		this.setGlassEnabled(true);
		this.setModal(true);
		this.setWidget(preloaderWidget);
		ReflectionProgressBar reflectionProgressBar = new ReflectionProgressBar();
		preloaderWidget.add(reflectionProgressBar);
	}

}
