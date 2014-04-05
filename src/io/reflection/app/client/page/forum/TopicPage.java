//
//  TopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.client.page.Page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class TopicPage extends Page {

	private static TopicPageUiBinder uiBinder = GWT.create(TopicPageUiBinder.class);

	interface TopicPageUiBinder extends UiBinder<Widget, TopicPage> {}

	public TopicPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
