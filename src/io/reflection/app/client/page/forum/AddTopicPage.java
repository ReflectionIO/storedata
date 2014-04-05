//
//  AddTopicPage.java
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
public class AddTopicPage extends Page {

	private static AddTopicPageUiBinder uiBinder = GWT.create(AddTopicPageUiBinder.class);

	interface AddTopicPageUiBinder extends UiBinder<Widget, AddTopicPage> {}

	public AddTopicPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
