//
//  PostSummaryCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.blog.part;

import java.io.IOException;

import io.reflection.app.client.helper.MarkdownHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class PostSummaryCell extends AbstractCell<Post> {

	interface PostSummaryCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, SafeUri link, String title, SafeHtml description, String author, String published);
	}

	private static PostSummaryCellRenderer RENDERER = GWT.create(PostSummaryCellRenderer.class);

	@Override
	public void render(Context context, Post value, SafeHtmlBuilder builder) {
		SafeUri link = PageType.BlogPostPageType.asHref("view", value.id.toString());
		String published = "TBD";

		if (value.published != null) {
			published = DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_EEE_DD_MMM_YYYY).format(value.published);
		}

		String processedString = value.description;

		try {
			processedString = MarkdownHelper.PROCESSOR.process(value.description);
		} catch (IOException e) {
			new RuntimeException(e);
		}

		if (value.visible == Boolean.TRUE) {
			processedString += "<p>NOT PUBLISHED</p>";
		}

		RENDERER.render(builder, link, value.title, SafeHtmlUtils.fromTrustedString(processedString), FormattingHelper.getUserName(value.author), published);
	}

}
