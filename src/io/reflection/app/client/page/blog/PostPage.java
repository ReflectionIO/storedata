//
//  PostPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.client.page.Page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 *
 */
public class PostPage extends Page {

	private static PostPageUiBinder uiBinder = GWT.create(PostPageUiBinder.class);

	interface PostPageUiBinder extends UiBinder<Widget, PostPage> {}

	public PostPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
