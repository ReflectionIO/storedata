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
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author billy1380
 * 
 */
public class ForumMessageCell extends AbstractCell<ForumMessage> {
	private RichTextArea richText;

	/**
	 * @param consumedEvents
	 */
	public ForumMessageCell(String... consumedEvents) {
		super(BrowserEvents.CLICK);
	}

	interface ForumMessageCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String authorName, SafeHtml content, String created, Long topicId, Long messageId);

		void onBrowserEvent(ForumMessageCell o, NativeEvent e, Element p, ForumMessage n);
	}

	private static ForumMessageCellRenderer RENDERER = GWT.create(ForumMessageCellRenderer.class);

	/**
	 * The HTML templates used to render the cell.
	 */
	interface QuoteTemplate extends SafeHtmlTemplates {
		QuoteTemplate INSTANCE = GWT.create(QuoteTemplate.class);

		@SafeHtmlTemplates.Template("<div class=\"forumMessageQuote\"><div class=\"forumMessageQuoteAuthor\">{0} said : {1}</div>")
		SafeHtml quoteLayout(SafeHtml author, SafeHtml message);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, ForumMessage value, NativeEvent event, ValueUpdater<ForumMessage> valueUpdater) {
		// SafeUri link = PageType.ForumEditTopicPageType.asHref("edit", value.getTopicId().toString());
		RENDERER.onBrowserEvent(this, event, parent, value);
	}

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

	@UiHandler("quote")
	void focusReplyClicked(ClickEvent event, Element parent, ForumMessage value) {
		Document.get().setScrollTop(richText.getAbsoluteTop());

		richText.setFocus(true);

		richText.setHTML(QuoteTemplate.INSTANCE.quoteLayout(SafeHtmlUtils.fromString(FormattingHelper.getUserName(value.getAuthor())),
				SafeHtmlUtils.fromTrustedString(value.getContent())));
	}

	/**
	 * @param richText
	 */
	public void setRichText(RichTextArea richText) {
		this.richText = richText;
	}

	public RichTextArea getRichText() {
		return richText;
	}

}
