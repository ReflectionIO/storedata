//
//  MessageCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.part.datatypes.Message;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class MessageCell extends AbstractCell<Message> {

	interface ForumSummaryCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, SafeUri link, String title, String styleName, String description, Integer topics);
	}

	private static ForumSummaryCellRenderer RENDERER = GWT.create(ForumSummaryCellRenderer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Message value, SafeHtmlBuilder builder) {
		RENDERER.render(builder, null, null, null, null, null);
	}

}
