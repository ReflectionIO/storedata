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
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author billy1380
 * 
 */

public class ForumMessageCell extends AbstractCell<ForumMessage> {
	@UiField RichTextArea replyText;

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
    	/**
       * The template for this Cell, which includes styles and a value.
       * 
       * @param styles the styles to include in the style attribute of the div
       * @param value the safe value. Since the value type is {@link SafeHtml},
       *          it will not be escaped before including it in the template.
       *          Alternatively, you could make the value type String, in which
       *          case the value would be escaped.
       * @return a {@link SafeHtml} instance
       */
      //@SafeHtmlTemplates.Template("<div class=\"forumMessageQuote\"><div class'\=\"forumMessageQuoteAuthor\">{0} said :</div>")
      SafeHtml cell(SafeStyles styles, SafeHtml value);
      SafeHtml appendQuote(SafeHtml userName, SafeHtml quote);
    }


    @Override
    public void onBrowserEvent(Context context, Element parent, ForumMessage value,
            NativeEvent event, ValueUpdater<ForumMessage> valueUpdater) {
    	    SafeUri link = PageType.ForumEditTopicPageType.asHref("edit", value.getTopicId().toString());

    		
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
	@UiHandler("edit")
    void onEditButtonClicked(ClickEvent event, Element parent, ForumMessage value) {
		
		
		
    }
	
	@UiHandler("quote")
	void focusReplyClicked(ClickEvent event, Element parent, ForumMessage value) {
		Window.alert("Do you want to quote? : " + value.getContent());
		replyText.setFocus(true);
		Document.get().setScrollTop(replyText.getAbsoluteTop());
	}
 

}
