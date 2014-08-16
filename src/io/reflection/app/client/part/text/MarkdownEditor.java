//
//  MarkdownEditor.java
//  storedata
//
//  Created by daniel on 7 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.text;

import io.reflection.app.client.helper.MarkdownHelper;
import io.reflection.app.client.part.BootstrapGwtTabPanel;

import java.io.IOException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author daniel
 * 
 */
public class MarkdownEditor extends Composite implements HasText {

	private static MarkdownEditorUiBinder uiBinder = GWT.create(MarkdownEditorUiBinder.class);

	@UiField TextArea textArea;
	@UiField DivElement preview;
	@UiField TabPanel tabLayout;

	@UiField Button bold;
	@UiField Button italic;

	interface MarkdownEditorUiBinder extends UiBinder<Widget, MarkdownEditor> {}

	public MarkdownEditor() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtTabPanel.INSTANCE.styles().ensureInjected();

		bold.getElement().setInnerHTML("<span class=\"icon-bold\"></span>");
		italic.getElement().setInnerHTML("<span class=\"icon-italic\"></span>");

		tabLayout.selectTab(0);

		tabLayout.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {

			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				// we're about to change tabs
				Integer index = event.getItem();

				if (index == 1) {
					// styles to be included in the header.

					try {
						// FIXME SafeHtmlUtils.htmlEscape is too rough need a markdown aware plugin - using nothing is exploitable
						String previewHtml = MarkdownHelper.PROCESSOR.process(textArea.getText());
						preview.removeAllChildren();
						preview.setInnerHTML(previewHtml);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});

		bold.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				wrapText("**", "**");
			}
		});

		italic.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				wrapText("*", "*");
			}
		});
	}

	public MarkdownEditor(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	@Override
	public String getText() {
		return textArea.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		textArea.setText(text);
	}

	protected void wrapText(String leftWrap, String rightWrap) {
		String selection = textArea.getSelectedText();
		if (selection.length() > 0) {
			int position = textArea.getCursorPos();
			String unedited = textArea.getText();

			String newString = unedited.substring(0, position) + leftWrap + selection + rightWrap
					+ unedited.substring(position + selection.length(), unedited.length());

			textArea.setText(newString);
		}
	}

	/**
	 * @param b
	 */
	public void setFocus(boolean b) {
		textArea.setFocus(b);
	}

	/**
	 * @param content
	 */
	public void insertQuote(String author, String content) {
		// http://stackoverflow.com/questions/14217101/what-character-represents-a-new-line-in-a-text-area
		String CRLF = "\r\n"; // not necessarily a complete solution across all browsers. Needs more testing.

		int position = textArea.getCursorPos();
		String unedited = textArea.getText();

		content = content.replaceAll(CRLF, CRLF + ">");
		String initial = CRLF + ">";
		if (position == 0) {
			initial = ">";
		}

		String newString = unedited.substring(0, position) + initial + author + " wrote:" + CRLF + content + CRLF
				+ unedited.substring(position, unedited.length());

		textArea.setText(newString);
	}

}
