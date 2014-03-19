//
//  DisplayTag.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class DisplayTag extends Composite {

	private static DisplayTagUiBinder uiBinder = GWT.create(DisplayTagUiBinder.class);

	interface DisplayTagUiBinder extends UiBinder<Widget, DisplayTag> {}

	@UiField SpanElement name;
	@UiField InlineHyperlink link;

	public DisplayTag() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * @param tag
	 */
	public void setName(String tag) {
		name.setInnerHTML(tag);
		link.setTargetHistoryToken("tag/view/" + tag.replace(" ", "-"));
	}

}
