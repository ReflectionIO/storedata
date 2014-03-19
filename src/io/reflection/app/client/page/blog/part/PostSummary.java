//
//  PostSummary.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog.part;

import io.reflection.app.datatypes.shared.Post;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class PostSummary extends Composite {

	private static PostSummaryUiBinder uiBinder = GWT.create(PostSummaryUiBinder.class);

	interface PostSummaryUiBinder extends UiBinder<Widget, PostSummary> {}

	@UiField InlineHyperlink title;

	@UiField SpanElement date;
	@UiField SpanElement author;

	@UiField ParagraphElement description;

	@UiField InlineHyperlink readMore;

	@UiField HTMLPanel tags;

	public PostSummary() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPost(Post post) {
		if (post != null) {
			title.setText(post.title);
			title.setTargetHistoryToken("blogpost/view/" + post.id.toString());

			if (post.published != null) {
				date.setInnerText(DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(post.published));
			} else {
				date.setInnerText("TBD");
			}

			author.setInnerText(post.author.forename + " " + post.author.surname);

			description.setInnerHTML(post.description);

			readMore.setTargetHistoryToken("blogpost/view/" + post.id.toString());

			if (tags.getWidgetCount() > 0) {
				tags.clear();
			}

			if (post.tags != null) {
				DisplayTag displayTag;
				for (String tag : post.tags) {
					displayTag = new DisplayTag();
					displayTag.setName(tag);
					displayTag.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					tags.add(displayTag);
				}
			}
		}
	}

}
