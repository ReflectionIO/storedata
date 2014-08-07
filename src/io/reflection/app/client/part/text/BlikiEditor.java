//
//  BlikiEditor.java
//  storedata
//
//  Created by daniel on 7 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.text;

import info.bliki.wiki.model.WikiModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * @author daniel
 *
 */
public class BlikiEditor extends Composite implements HasText{

	private static BlikiEditorUiBinder uiBinder = GWT.create(BlikiEditorUiBinder.class);
	@UiField TextArea textArea;
	@UiField Frame iframe;
	@UiField TabLayoutPanel tabLayout;

	interface BlikiEditorUiBinder extends UiBinder<Widget, BlikiEditor> {}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public BlikiEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		
		textArea.getElement().addClassName("form-control");
		iframe.getElement().addClassName("form-control");
		tabLayout.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>(){

			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				//we're about to change tabs
				Integer indx = event.getItem();
				if (indx == 1)
				{
					String htmlText = WikiModel.toHtml("This is a simple [[Hello World]] wiki tag");
					FrameElement frameElement = (FrameElement) (Object) iframe.getElement();
					
					Document contentDocument = frameElement.getContentDocument(); 
	                Element targetElement = contentDocument.getElementsByTagName("head") 
	                                .getItem(0); 

	                if(targetElement == null){ 
	                        targetElement = contentDocument.getDocumentElement() 
	                                        .getFirstChildElement(); 

	                if(targetElement == null){ 
	                        contentDocument.insertFirst(targetElement = contentDocument 
	                                        .createElement("head")); 
	                        } 
	                }
	                targetElement.appendChild(contentDocument.createTextNode("Blalalalalalalalalalla"));

					
				}
				
				
			}	
		});
	}

	public BlikiEditor(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

		
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		
	}

	

}
