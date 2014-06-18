//
//  ForumMessageCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author billy1380
 * 
 */

public class ForumMessageCell extends AbstractCell<ForumMessage> {
	@UiField RichTextArea replyText;

	interface ForumMessageCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String authorName, SafeHtml content, String created, Long topicId, Long messageId);
	}

	private static ForumMessageCellRenderer RENDERER = GWT.create(ForumMessageCellRenderer.class);


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, ForumMessage value, SafeHtmlBuilder builder) {

		// put template string empty or template with button
		// id insetad username
		// value has author
		// ForumMessage ad property to check the user
		RENDERER.render(builder, FormattingHelper.getUserLongName(value.getAuthor()), SafeHtmlUtils.fromTrustedString(value.getContent()), "Posted "
				+ FormattingHelper.getTimeSince(value.getCreated()), value.getTopicId(), value.getId());
	}

}
