//
//  BlogSidePanel.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 10 Jul 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class BlogSidePanel extends Composite {

	private static BlogSidePanelUiBinder uiBinder = GWT.create(BlogSidePanelUiBinder.class);

	interface BlogSidePanelUiBinder extends UiBinder<Widget, BlogSidePanel> {}

	@UiField InlineHyperlink blogHomeLink;

	public BlogSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public InlineHyperlink getMyAppsLink() {
		return blogHomeLink;
	}

	public void setBlogHomeLinkActive() {
		blogHomeLink.getElement().setAttribute("style", "color: #000");
	}

}
