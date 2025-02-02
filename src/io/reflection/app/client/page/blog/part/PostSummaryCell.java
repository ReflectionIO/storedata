//
//  PostSummaryCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page.blog.part;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMATTER_EEE_DD_MMM_YYYY;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.helper.ColorHelper;
import io.reflection.app.client.helper.MarkdownHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
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

	interface DateTemplate extends SafeHtmlTemplates {
		DateTemplate INSTANCE = GWT.create(DateTemplate.class);

		@SafeHtmlTemplates.Template("<span style=\"color: {0}\">NOT PUBLISHED</span>")
		SafeHtml notPublished(String color);

		@SafeHtmlTemplates.Template("<span>{0}</span>")
		SafeHtml publishedDate(String formattedDate);

		@SafeHtmlTemplates.Template("<span style=\"color: {0}\">NOT VISIBLE</span>")
		SafeHtml notVisible(String color);

	}

	interface PostSummaryCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, SafeUri link, String title, SafeHtml description, String author, SafeHtml published);
	}

	private static PostSummaryCellRenderer RENDERER = GWT.create(PostSummaryCellRenderer.class);

	@Override
	public void render(Context context, Post value, SafeHtmlBuilder builder) {
		String s = value.code == null || value.code.length() == 0 ? value.id.toString() : value.code;

		SafeUri link = PageType.BlogPostPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, s);
		SafeHtml published = DateTemplate.INSTANCE.notPublished(ColorHelper.getReflectionRed());

		if (value.published != null) {
			published = DateTemplate.INSTANCE.publishedDate(DATE_FORMATTER_EEE_DD_MMM_YYYY.format(value.published));
		}

		String processedString = MarkdownHelper.process(value.description);

		if (value.visible == Boolean.FALSE) {
			processedString += DateTemplate.INSTANCE.notVisible(ColorHelper.getReflectionRed()).asString();
		}

		RENDERER.render(builder, link, value.title, SafeHtmlUtils.fromTrustedString(processedString), FormattingHelper.getUserName(value.author), published);
	}
}
