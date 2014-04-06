//
//  ForumSummaryCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Forum;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class ForumSummaryCell extends AbstractCell<Forum> {

	interface ForumSummaryCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, SafeUri link, String title, String styleName, String description, Integer topics);
	}

	private static ForumSummaryCellRenderer RENDERER = GWT.create(ForumSummaryCellRenderer.class);

	@Override
	public void render(Context context, Forum value, SafeHtmlBuilder builder) {
		SafeUri link = PageType.ForumPageType.asHref("view", value.id.toString());

		String styleName = "list-group-item";
		if (value.id.equals(TopicController.get().getForumId())) {
			styleName += " active";
		}

		RENDERER.render(builder, link, value.title, styleName, value.description, value.numberOfTopics);
	}
}
