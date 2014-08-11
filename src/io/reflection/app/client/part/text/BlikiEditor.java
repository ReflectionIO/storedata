//
//  BlikiEditor.java
//  storedata
//
//  Created by daniel on 7 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * @author daniel
 * 
 */
public class BlikiEditor extends Composite implements HasText {

	private static BlikiEditorUiBinder uiBinder = GWT.create(BlikiEditorUiBinder.class);
	@UiField TextArea textArea;
	@UiField Frame iframe;
	@UiField TabPanel tabLayout;

	@UiField Button bold;
	@UiField Button italic;

	interface BlikiEditorUiBinder extends UiBinder<Widget, BlikiEditor> {}

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public BlikiEditor() {
		initWidget(uiBinder.createAndBindUi(this));

		textArea.getElement().addClassName("form-control");
		iframe.getElement().addClassName("form-control");

		// Processor.process("Change me int **markup** now please.");
		// String test = processor.markdown("whatever **so** cool");

		bold.getElement().setInnerHTML("<span class=\"icon-bold\"></span>");
		italic.getElement().setInnerHTML("<span class=\"icon-italic\"></span>");

		// tabLayout.getTabBar()

		tabLayout.selectTab(0);

		tabLayout.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {

			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				// we're about to change tabs
				Integer indx = event.getItem();

				if (indx == 1) {
					FrameElement frameElement = (FrameElement) (Object) iframe.getElement();

					Document contentDocument = frameElement.getContentDocument();
					Element bodyElement = contentDocument.getElementsByTagName("body").getItem(0);

					if (bodyElement == null) {
						contentDocument.insertFirst(bodyElement = contentDocument.createElement("body"));
					}
					bodyElement.removeAllChildren();

					// styles to be included in the header.
					// <link rel="stylesheet" href="bootstrap-v3.1.1/css/bootstrap.min.css">
					// <link rel="stylesheet" href="reflectionglphs-v5/css/reflectionglyphs.css">
					// <link href="favicon.ico" rel="icon" type="image/x-icon">
					// <link href="//fonts.googleapis.com/css?family=Source+Sans+Pro:400,600" rel="stylesheet" type="text/css">
					// <link href="//fonts.googleapis.com/css?family=Lato:400,700" rel="stylesheet" type="text/css">
					// <link href="//fonts.googleapis.com/css?family=Oswald" rel="stylesheet" type="text/css">

					bodyElement.appendChild(contentDocument.createTextNode("Blalalalalalalalalalla"));
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

	public BlikiEditor(String firstName) {
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
		String selection = textArea.getSelectedText() ;
		if (selection.length() > 0)
		{
			int position = textArea.getCursorPos() ;
			String unedited = textArea.getText() ;
			
			String newString = unedited.substring(0, position) + leftWrap +
					selection + rightWrap +
					unedited.substring(position + selection.length(), unedited.length()) ;
			
			textArea.setText(newString);
		}
	}
}
