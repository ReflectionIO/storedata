//
//  ForumMessageCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.helper.MarkdownHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.shared.util.FormattingHelper;

import java.io.IOException;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class ForumMessageCell extends AbstractCell<ForumMessage> {
	private MarkdownEditor markdownTextEditor;

	@UiField private AnchorElement flagButton;

	/**
	 * @param consumedEvents
	 */
	public ForumMessageCell(String... consumedEvents) {
		super(BrowserEvents.CLICK);
	}

	interface ForumMessageCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String authorName, SafeHtml content, String created, SafeHtml flagButtonHtml, SafeHtml editButtonHtml, SafeUri link);

		void onBrowserEvent(ForumMessageCell o, NativeEvent e, Element p, ForumMessage n);

	}

	interface TopicCss extends CssResource {
		String companyRow();
	}

	private static ForumMessageCellRenderer RENDERER = GWT.create(ForumMessageCellRenderer.class);

	/**
	 * The HTML templates used to render the cell.
	 */
	interface QuoteTemplate extends SafeHtmlTemplates {
		QuoteTemplate INSTANCE = GWT.create(QuoteTemplate.class);

		@SafeHtmlTemplates.Template("<a href=\"flag\" class=\"forumMessageLink\" ui:field=\"flagButton\"><i class=\"glyphicon glyphicon-flag\"></i>Flag</a> | ")
		SafeHtml flagButton();

		@SafeHtmlTemplates.Template("<a href=\"{0}/view/{1}/edit/{2}\" class=\"forumMessageLink\" ui:field=\"editButton\">Edit</a> | ")
		SafeHtml editButton(String pageHref, long topicId, long messageId);

	}

	@Override
	public void onBrowserEvent(Context context, Element parent, ForumMessage value, NativeEvent event, ValueUpdater<ForumMessage> valueUpdater) {
		// SafeUri link = PageType.ForumEditTopicPageType.asHref("edit",
		// value.getTopicId().toString());
		RENDERER.onBrowserEvent(this, event, parent, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client .Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, ForumMessage value, SafeHtmlBuilder builder) {

		// The CellList will render rows of nulls if the paging goes beyond the end of the list
		if (value != null) {
			SafeHtml editButtonHtml = QuoteTemplate.INSTANCE.editButton(PageType.ForumEditTopicPageType.asHref().asString(), value.getTopicId(), value.getId());

			// Enable this when we when we have the data to demonstrate both
			// cases. [purple highlighting for Reflection company posts]

			// if (value.getAuthor().company.equals("Reflection"))
			// color = css.companyRowClass();

			String processedString = value.getContent();

			try {
				processedString = MarkdownHelper.PROCESSOR.process(value.getContent());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			RENDERER.render(builder, FormattingHelper.getUserName(value.getAuthor()), SafeHtmlUtils.fromTrustedString(processedString), FormattingHelper
					.getTimeSince(value.getCreated()), value.belongsToCurrentUser() ? SafeHtmlUtils.EMPTY_SAFE_HTML : QuoteTemplate.INSTANCE.flagButton(),
					value.belongsToCurrentUser() ? editButtonHtml : SafeHtmlUtils.EMPTY_SAFE_HTML, UriUtils.fromSafeConstant(PageType.ForumThreadPageType
							.asHref("view", value.getTopicId().toString(), "post", Integer.toString(value.getIndex())).asString()));
		}

	}

	@UiHandler("quote")
	void focusReplyClicked(ClickEvent event, Element parent, ForumMessage value) {
		Document.get().setScrollTop(markdownTextEditor.getAbsoluteTop());

		markdownTextEditor.setFocus(true);
		markdownTextEditor.insertQuote(FormattingHelper.getUserName(value.getAuthor()), value.getContent());
	}

	/**
	 * @param value
	 */
	public void setMarkdownTextEditor(MarkdownEditor value) {
		markdownTextEditor = value;
	}

}