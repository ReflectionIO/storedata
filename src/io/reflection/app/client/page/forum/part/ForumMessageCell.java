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
import io.reflection.app.shared.util.FormattingHelper;

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
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author billy1380
 * 
 */
public class ForumMessageCell extends AbstractCell<ForumMessage> {
	private RichTextArea richText;

	@UiField private AnchorElement flagButton;

	/**
	 * @param consumedEvents
	 */
	public ForumMessageCell(String... consumedEvents) {
		super(BrowserEvents.CLICK);
	}

	interface ForumMessageCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String authorName, SafeHtml content, String created, SafeHtml flagButtonHtml, SafeHtml editButtonHtml, SafeUri link, String backgroundColour);

		void onBrowserEvent(ForumMessageCell o, NativeEvent e, Element p, ForumMessage n);

	}
	
	public interface TopicResources extends ClientBundle {
		  public static final TopicResources INSTANCE =  GWT.create(TopicResources.class);

		  @Source("topic.css")
		  public TopicCss css();

		}

	interface TopicCss extends CssResource {
		@ClassName("oddRow")
		String oddRowClass();
		
		@ClassName("evenRow")
		String evenRowClass();
	}

	private static ForumMessageCellRenderer RENDERER = GWT.create(ForumMessageCellRenderer.class);

	/**
	 * The HTML templates used to render the cell.
	 */
	interface QuoteTemplate extends SafeHtmlTemplates {
		QuoteTemplate INSTANCE = GWT.create(QuoteTemplate.class);

		@SafeHtmlTemplates.Template("<blockquote style=\"margin: 0 0 0 40px; border: none; padding: 0px;\"><i>{0} wrote:</i><div style=\"font-style: italic;\">"+
									"<span class=\"Apple-tab-span\" style=\"white-space:pre\">\t</span>\"{1}\"</div></blockquote><br/>")
		SafeHtml quoteLayout(SafeHtml author, SafeHtml message);

		@SafeHtmlTemplates.Template("<a href=\"flag\" class=\"btn btn-warning btn-xs\" ui:field=\"flagButton\"><i class=\"glyphicon glyphicon-flag\"></i>Flag</a>")
		SafeHtml flagButton();

		@SafeHtmlTemplates.Template("<a href=\"{0}/view/{1}/edit/{2}\" class=\"btn btn-default btn-xs\" ui:field=\"editButton\"><i class=\"glyphicon glyphicon-pencil\"></i>Edit</a>")
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
		if (value != null)
		{
			// put template string empty or template with button
			// id insetad username
			// value has author
			// ForumMessage ad property to check the user
			SafeHtml editButtonHtml = QuoteTemplate.INSTANCE.editButton(PageType.ForumEditTopicPageType.asHref().asString(), value.getTopicId(), value.getId());
			
			String color = css.oddRowClass();
			if (context.getIndex() % 2 == 1)
				color = css.evenRowClass();
	
			RENDERER.render(builder, FormattingHelper.getUserLongName(value.getAuthor()), SafeHtmlUtils.fromTrustedString(value.getContent()), "Posted "
					+ FormattingHelper.getTimeSince(value.getCreated()),
					value.belongsToCurrentUser() ? QuoteTemplate.INSTANCE.empty() : QuoteTemplate.INSTANCE.flagButton(),
					value.belongsToCurrentUser() ? editButtonHtml : QuoteTemplate.INSTANCE.empty(),
							UriUtils.fromSafeConstant(PageType.ForumThreadPageType.asHref()+"/view/"+value.getTopicId()+"/post/"+value.getIndex()), 
							color);
			
			
		}

	}

	@UiHandler("quote")
	void focusReplyClicked(ClickEvent event, Element parent, ForumMessage value) {
		Document.get().setScrollTop(richText.getAbsoluteTop());

		richText.setFocus(true);

		richText.getFormatter().insertHTML(QuoteTemplate.INSTANCE.quoteLayout(SafeHtmlUtils.fromString(FormattingHelper.getUserName(value.getAuthor())),
				SafeHtmlUtils.fromTrustedString(value.getContent())).asString());

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
