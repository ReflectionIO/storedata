//
//  BlikiEditor.java
//  storedata
//
//  Created by daniel on 7 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.text;

import java.io.IOException;

import org.markdown4j.Markdown4jProcessor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author daniel
 * 
 */
public class BlikiEditor extends Composite implements HasText {

    private static BlikiEditorUiBinder uiBinder = GWT.create(BlikiEditorUiBinder.class);
    @UiField
    TextArea textArea;
    @UiField
    Frame iframe;
    @UiField
    TabPanel tabLayout;

    @UiField
    Button bold;
    @UiField
    Button italic;

    interface BlikiEditorUiBinder extends UiBinder<Widget, BlikiEditor> {
    }

    /**
     * Because this class has a default constructor, it can be used as a binder
     * template. In other words, it can be used in other *.ui.xml files as
     * follows: <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
     * xmlns:g="urn:import:**user's package**">
     * <g:**UserClassName**>Hello!</g:**UserClassName> </ui:UiBinder> Note that
     * depending on the widget that is used, it may be necessary to implement
     * HasHTML instead of HasText.
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

                    // styles to be included in the header.

                    String header = "<meta http-equiv='content-type' content='text/html; charset=UTF-8'>"
                            + "<link rel='stylesheet' href='/bootstrap-v3.1.1/css/bootstrap.min.css'>\n"
                            + "<link rel='stylesheet' href='/reflectionglphs-v5/css/reflectionglyphs.css'>\n"
                            + "<link href='favicon.ico' rel='icon' type='image/x-icon'>\n"
                            + "<link href='//fonts.googleapis.com/css?family=Source+Sans+Pro:400,600' rel='stylesheet' type='text/css'>\n"
                            + "<link href='//fonts.googleapis.com/css?family=Lato:400,700' rel='stylesheet' type='text/css'>\n"
                            + "<link href='//fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>";

                    try {
                        String previewHtml = new Markdown4jProcessor().process(textArea.getText());

                        // This doesn't work... because it is tempting to think
                        // it would
                        // frameElement.setInnerHTML(previewHtml);

                        BlikiEditor.this.fillIframe(frameElement, previewHtml, header);

                        // bodyElement.appendChild(contentDocument.createTextNode(previewHtml));
                    } catch (IOException e) {
                        e.printStackTrace();
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
     * http://bealetech.com/blog/2010/01/25/embedding-html-document-in-an-iframe
     * -with-gwt/ and an alternative
     * https://groups.google.com/forum/#!topic/google-web-toolkit/mjzFLq8s1v4
     * 
     * */
    private final native void fillIframe(FrameElement iframe, String content, String header) /*-{
                                                                                             var doc = iframe.document;

                                                                                             if (iframe.contentDocument)
                                                                                             doc = iframe.contentDocument; // For NS6
                                                                                             else if (iframe.contentWindow)
                                                                                             doc = iframe.contentWindow.document; // For IE5.5 and IE6

                                                                                             // Put the content in the iframe
                                                                                             doc.open();
                                                                                             doc.writeln('<head>' + header + '</head>');
                                                                                             doc.writeln('<body>' + content + '</body>');
                                                                                             doc.close();
                                                                                             }-*/;

    /**
     * @param content
     */
    public void insertQuote(String content) {

        int position = textArea.getCursorPos();
        String unedited = textArea.getText();

        content = content.replaceAll("\n", "\n>");
        String initial = "\n>";
        if (position == 0) {
            initial = ">";
        }

        String newString = unedited.substring(0, position) + initial + content + "\n" + unedited.substring(position, unedited.length());

        textArea.setText(newString);

    }

}
