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
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.Window;

/**
 * @author billy1380
 * 
 */
public class ForumMessageCell extends AbstractCell<ForumMessage> {
	private MarkdownEditor markdownTextEditor;

	/**
	 * @param consumedEvents
	 */
	public ForumMessageCell(String... consumedEvents) {
		super(BrowserEvents.CLICK);
	}

	interface ForumMessageCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String authorName, SafeHtml content, String created, SafeUri link, String flagBar, String flagText, String editBar,
				SafeHtml editButtonHtml, String quoteBar, String quoteText);

		void onBrowserEvent(ForumMessageCell o, NativeEvent e, Element p, ForumMessage n);

	}

	private static ForumMessageCellRenderer RENDERER = GWT.create(ForumMessageCellRenderer.class);

	/**
	 * The HTML templates used to render the cell.
	 */
	interface QuoteTemplate extends SafeHtmlTemplates {
		QuoteTemplate INSTANCE = GWT.create(QuoteTemplate.class);

		@SafeHtmlTemplates.Template("<a href=\"{0}/view/{1}/edit/{2}\" class=\"forumMessageLink\">Edit</a>")
		SafeHtml replyEditButton(String pageHref, long topicId, long messageId);

		@SafeHtmlTemplates.Template("<a href=\"{0}/edit/{1}\" class=\"forumMessageLink\">Edit</a>")
		SafeHtml topicEditButton(String pageHref, long topicId);

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
			SafeHtml editButtonHtml = null;

			if (value.isTopic()) {
				editButtonHtml = QuoteTemplate.INSTANCE.topicEditButton(PageType.ForumEditTopicPageType.asHref().asString(), value.getTopicId());
			} else {
				editButtonHtml = QuoteTemplate.INSTANCE.replyEditButton(PageType.ForumEditTopicPageType.asHref().asString(), value.getTopicId(), value.getId());
			}

			String processedString = value.getContent();

			try {
				processedString = MarkdownHelper.PROCESSOR.process(value.getContent());
			} catch (IOException e) {
				new RuntimeException(e);
			}

			SafeUri link = UriUtils.fromSafeConstant(PageType.ForumThreadPageType.asHref("view", value.getTopicId().toString(), "view",
					Integer.toString(value.getIndex())).asString());

			RENDERER.render(builder, FormattingHelper.getUserName(value.getAuthor()), SafeHtmlUtils.fromTrustedString(processedString), FormattingHelper
					.getTimeSince(value.getCreated()), link, value.canFlag() ? " | " : "", value.canFlag() ? "Flag" : "", value.canEdit() ? " | " : "", value
					.canEdit() ? editButtonHtml : SafeHtmlUtils.EMPTY_SAFE_HTML, value.canQuote() ? " | " : "", value.canQuote() ? "Quote" : "");
		}

	}

	@UiHandler("flag")
	void flagClicked(ClickEvent event, Element parent, ForumMessage value) {
		if (value.canFlag()) {
			if (value.isTopic()) {
				// use topic controller to flag
				Window.alert("Cannot flag topics yet!");
			} else {
				// use reply controller to flag
				Window.alert("Cannot flag responses yet!");
			}
		}
	}

	@UiHandler("quote")
	void quoteClicked(ClickEvent event, Element parent, ForumMessage value) {
		if (value.canQuote()) {
			Document.get().setScrollTop(markdownTextEditor.getAbsoluteTop());

			markdownTextEditor.setFocus(true);
			markdownTextEditor.insertQuote(FormattingHelper.getUserName(value.getAuthor()), value.getContent());
		}
	}

	/**
	 * @param value
	 */
	public void setMarkdownTextEditor(MarkdownEditor value) {
		markdownTextEditor = value;
	}

}