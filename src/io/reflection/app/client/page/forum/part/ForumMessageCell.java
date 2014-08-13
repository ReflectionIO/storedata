//
//  ForumMessageCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.datatypes.ForumMessage;
import io.reflection.app.client.part.text.BlikiEditor;
import io.reflection.app.shared.util.FormattingHelper;

import java.io.IOException;

import org.markdown4j.Markdown4jProcessor;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ClientBundle;
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
	private BlikiEditor richText;

	@UiField private AnchorElement flagButton;

	/**
	 * @param consumedEvents
	 */
	public ForumMessageCell(String... consumedEvents) {
		super(BrowserEvents.CLICK);
	}

	interface ForumMessageCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String authorName, SafeHtml content, String created, SafeHtml flagButtonHtml, SafeHtml editButtonHtml, SafeUri link,
				String backgroundColour);

		void onBrowserEvent(ForumMessageCell o, NativeEvent e, Element p, ForumMessage n);

	}

	public interface TopicResources extends ClientBundle {
		public static final TopicResources INSTANCE = GWT.create(TopicResources.class);

		@Source("topic.css")
		public TopicCss css();

	}

	interface TopicCss extends CssResource {
		@ClassName("oddRow")
		String oddRowClass();

		@ClassName("evenRow")
		String evenRowClass();

		@ClassName("companyRow")
		String companyRowClass();
	}

	private static ForumMessageCellRenderer RENDERER = GWT.create(ForumMessageCellRenderer.class);

	/**
	 * The HTML templates used to render the cell.
	 */
	interface QuoteTemplate extends SafeHtmlTemplates {
		QuoteTemplate INSTANCE = GWT.create(QuoteTemplate.class);

		@SafeHtmlTemplates.Template("<a href=\"flag\" class=\"\" ui:field=\"flagButton\"><i class=\"glyphicon glyphicon-flag\"></i>Flag</a> | ")
		SafeHtml flagButton();

		@SafeHtmlTemplates.Template("<a href=\"{0}/view/{1}/edit/{2}\" class=\"\" ui:field=\"editButton\">Edit</a> | ")
		SafeHtml editButton(String pageHref, long topicId, long messageId);

		@SafeHtmlTemplates.Template("")
		SafeHtml empty();

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

		TopicCss css = TopicResources.INSTANCE.css();
		css.ensureInjected();

		/* The CellList will render rows of nulls if the paging goes beyond the end of the list */
		if (value != null) {
			SafeHtml editButtonHtml = QuoteTemplate.INSTANCE.editButton(PageType.ForumEditTopicPageType.asHref().asString(), value.getTopicId(), value.getId());

			String color = css.oddRowClass();
			if (context.getIndex() % 2 == 1) color = css.evenRowClass();

			// Enable this when we when we have the data to demonstrate both cases.

			// if (value.getAuthor().company.equals("Reflection"))
			// color = css.companyRowClass();

			String processedString = value.getContent();

			try {
				processedString = new Markdown4jProcessor().process(value.getContent());
			} catch (IOException e) {
				e.printStackTrace();
			}

			RENDERER.render(builder, FormattingHelper.getUserName(value.getAuthor()), SafeHtmlUtils.fromTrustedString(processedString), FormattingHelper
					.getTimeSince(value.getCreated()), value.belongsToCurrentUser() ? QuoteTemplate.INSTANCE.empty() : QuoteTemplate.INSTANCE.flagButton(),
					value.belongsToCurrentUser() ? editButtonHtml : QuoteTemplate.INSTANCE.empty(), UriUtils.fromSafeConstant(PageType.ForumThreadPageType
							.asHref().asString() + "/view/" + value.getTopicId() + "/post/" + value.getIndex()), color);
		}

	}

	@UiHandler("quote")
	void focusReplyClicked(ClickEvent event, Element parent, ForumMessage value) {
		Document.get().setScrollTop(richText.getAbsoluteTop());

		richText.setFocus(true);

		richText.insertQuote(value.getAuthor().forename + " wrote: \n" + value.getContent());

	}

	/**
	 * @param richText
	 */
	public void setRichText(BlikiEditor richText) {
		this.richText = richText;
	}

	public BlikiEditor getRichText() {
		return richText;
	}

}
