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
import io.reflection.app.client.part.Preloader;

import java.io.IOException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
    @UiField Button link;
    @UiField Button image;

    @UiField Preloader preloader;

    interface MarkdownEditorUiBinder extends UiBinder<Widget, MarkdownEditor> {}

    public MarkdownEditor() {
        initWidget(uiBinder.createAndBindUi(this));

        BootstrapGwtTabPanel.INSTANCE.styles().ensureInjected();

        bold.getElement().setInnerHTML("<span class=\"icon-bold\"></span>");
        italic.getElement().setInnerHTML("<span class=\"icon-italic\"></span>");
        link.getElement().setInnerHTML("<span class=\"icon-link\"></span>");
        image.getElement().setInnerHTML("<span class=\"icon-picture\"></span>");

        tabLayout.selectTab(0);

        tabLayout.getTabBar().setTabHTML(0, "Edit &nbsp;&nbsp;&nbsp;&nbsp;<span class=\"glyphicon glyphicon-pencil\"></span>");
        tabLayout.getTabBar().setTabHTML(1, "Preview &nbsp;&nbsp;&nbsp;&nbsp;<span class=\"glyphicon glyphicon-eye-open\"></span>");

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

    }

    @UiHandler("bold")
    void onBoldClicked(ClickEvent event) {
        if (!removeSurround("**")) {
            wrapText("**", "**");
        }
    }

    @UiHandler("italic")
    void onItalicClicked(ClickEvent event) {
        if (!removeSurround("_")) {
            wrapText("_", "_");
        }
    }

    @UiHandler("link")
    void onLinkClicked(ClickEvent event) {

        String url = Window.prompt("Enter a link URL:", "http://");
        if (url != null) {
            linkOrImage(url, url, false);
        }

    }

    @UiHandler("image")
    void onImageClicked(ClickEvent event) {
        String url = Window.prompt("Enter a image URL:", "http://");
        if (url != null) {
            linkOrImage(url, url, true);
        }

    }

    public void linkOrImage(String title, String url, boolean image) {
        int position = textArea.getCursorPos();
        String unedited = textArea.getText();

        String newString = unedited.substring(0, position) + (image ? "!" : "") + "[" + title + "](" + url + ")"
                + unedited.substring(position, unedited.length());
        textArea.setText(newString);
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

        // when the markdown is set always go to the edit tab
        tabLayout.selectTab(0);
    }

    protected void wrapText(String leftWrap, String rightWrap) {
        String selection = textArea.getSelectedText();
        if (selection.length() > 0) {
            int position = textArea.getCursorPos();
            String unedited = textArea.getText();

            String begin = unedited.substring(0, position);
            String end = unedited.substring(position + selection.length(), unedited.length());
            String newString = begin + leftWrap + selection + rightWrap + end;

            textArea.setText(newString);
            int leftSidePos = begin.length() + leftWrap.length();
            textArea.setSelectionRange(leftSidePos, selection.length());
        }
    }

    protected boolean removeSurround(String surrounding) {
        int start = textArea.getCursorPos();
        int length = textArea.getSelectionLength();
        int surroundLength = surrounding.length();

        boolean result = false;

        String text = textArea.getText();
        if (start - surroundLength < 0 || length == 0) {
            textArea.setSelectionRange(start, length); // put back selection
        } else if (text.substring(start - surroundLength, start).equals(surrounding)
                && text.substring(start + length, start + length + surroundLength).equals(surrounding)) {
            // remove surrounds
            String newText = text.substring(0, start - surroundLength) + text.substring(start, start + length)
                    + text.substring(start + length + surroundLength, text.length());
            textArea.setText(newText);
            textArea.setSelectionRange(start - surroundLength, length);
            result = true;
        } else {
            textArea.setSelectionRange(start, length); // put back selection
        }

        return result;
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

    /**
     * 
     */
    public void reset() {
        textArea.setText("");
        preview.setInnerHTML("");
        tabLayout.selectTab(0);
    }

    /**
     * @param b
     */
    public void setLoading(boolean show) {
        if (show) {
            preloader.show(true);
        } else {
            preloader.hide();
        }
    }

}
