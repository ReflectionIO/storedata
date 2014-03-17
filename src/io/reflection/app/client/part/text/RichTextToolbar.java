//
//  RichTextToolbar.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class RichTextToolbar extends Composite {

	private static final String ACTIVE_STYLE_NAME = "active";

	private static RichTextToolbarUiBinder uiBinder = GWT.create(RichTextToolbarUiBinder.class);

	interface RichTextToolbarUiBinder extends UiBinder<Widget, RichTextToolbar> {}

	private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] { RichTextArea.FontSize.XX_SMALL,
			RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE,
			RichTextArea.FontSize.X_LARGE, RichTextArea.FontSize.XX_LARGE };

	private RichTextArea richText;
	private Formatter formatter;
	private HandlerRegistration richTextKeyUpRegistration;
	private HandlerRegistration richTextClickRegistration;

	@UiField Button bold;
	@UiField Button italic;
	@UiField Button underline;
	@UiField Button subscript;
	@UiField Button superscript;
	@UiField Button strikethrough;
	@UiField PushButton indent;
	@UiField PushButton outdent;
	@UiField PushButton justifyLeft;
	@UiField PushButton justifyCenter;
	@UiField PushButton justifyRight;
	@UiField PushButton hr;
	@UiField PushButton ol;
	@UiField PushButton ul;
	@UiField PushButton insertImage;
	@UiField PushButton createLink;
	@UiField PushButton removeLink;
	@UiField PushButton removeFormat;

	@UiField ListBox backColors;
	@UiField ListBox foreColors;
	@UiField ListBox fonts;
	@UiField ListBox fontSizes;

	public RichTextToolbar() {
		initWidget(uiBinder.createAndBindUi(this));

		bold.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-bold\"></span>");
		italic.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-italic\"></span>");
		underline.setText("underline");
		strikethrough.setText("strike through");

		superscript.setText("super");
		subscript.setText("sub");

		justifyLeft.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-align-left\"></span>");
		justifyCenter.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-align-center\"></span>");
		justifyRight.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-align-right\"></span>");

		indent.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-indent-right\"></span>");
		outdent.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-indent-left\"></span>");

		hr.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-minus\"></span>");

		ol.setText("ordered list");
		ul.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-list\"></span>");

		insertImage.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-picture\"></span>");

		createLink.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-link\"></span>");
		removeLink.setText("unlink");

		removeFormat.getElement().setInnerHTML("<span class=\"glyphicon glyphicon-ban-circle\"></span>");
	}

	@UiHandler({ "backColors", "foreColors", "fonts", "fontSizes" })
	void onChange(ChangeEvent event) {
		Widget sender = (Widget) event.getSource();

		if (sender == backColors) {
			formatter.setBackColor(backColors.getValue(backColors.getSelectedIndex()));
			backColors.setSelectedIndex(0);
		} else if (sender == foreColors) {
			formatter.setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
			foreColors.setSelectedIndex(0);
		} else if (sender == fonts) {
			formatter.setFontName(fonts.getValue(fonts.getSelectedIndex()));
			fonts.setSelectedIndex(0);
		} else if (sender == fontSizes) {
			formatter.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
			fontSizes.setSelectedIndex(0);
		}
	}

	@UiHandler({ "bold", "italic", "underline", "subscript", "superscript", "strikethrough", "indent", "outdent", "justifyLeft", "justifyCenter",
			"justifyRight", "hr", "ol", "ul", "insertImage", "createLink", "removeLink", "removeFormat" })
	void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();

		if (sender == bold) {
			formatter.toggleBold();
			toggle(bold);
		} else if (sender == italic) {
			formatter.toggleItalic();
			toggle(italic);
		} else if (sender == underline) {
			formatter.toggleUnderline();
			toggle(underline);
		} else if (sender == subscript) {
			formatter.toggleSubscript();
			toggle(subscript);
		} else if (sender == superscript) {
			formatter.toggleSuperscript();
			toggle(subscript);
		} else if (sender == strikethrough) {
			formatter.toggleStrikethrough();
			toggle(strikethrough);
		} else if (sender == indent) {
			formatter.rightIndent();
		} else if (sender == outdent) {
			formatter.leftIndent();
		} else if (sender == justifyLeft) {
			formatter.setJustification(RichTextArea.Justification.LEFT);
		} else if (sender == justifyCenter) {
			formatter.setJustification(RichTextArea.Justification.CENTER);
		} else if (sender == justifyRight) {
			formatter.setJustification(RichTextArea.Justification.RIGHT);
		} else if (sender == insertImage) {
			String url = Window.prompt("Enter an image URL:", "http://");
			if (url != null) {
				formatter.insertImage(url);
			}
		} else if (sender == createLink) {
			String url = Window.prompt("Enter a link URL:", "http://");
			if (url != null) {
				formatter.createLink(url);
			}
		} else if (sender == removeLink) {
			formatter.removeLink();
		} else if (sender == hr) {
			formatter.insertHorizontalRule();
		} else if (sender == ol) {
			formatter.insertOrderedList();
		} else if (sender == ul) {
			formatter.insertUnorderedList();
		} else if (sender == removeFormat) {
			formatter.removeFormat();
		} else if (sender == richText) {
			updateStatus();
		}
	}

	/**
	 * @param button
	 */
	private void toggle(Button button) {
		if (button.getStyleName().contains(ACTIVE_STYLE_NAME)) {
			button.removeStyleName(ACTIVE_STYLE_NAME);
		} else {
			button.addStyleName(ACTIVE_STYLE_NAME);
		}
	}

	/**
	 * Updates the status of all the stateful buttons.
	 */
	private void updateStatus() {
		if (formatter != null) {
			setActive(bold, formatter.isBold());
			setActive(italic, formatter.isItalic());
			setActive(underline, formatter.isUnderlined());
			setActive(subscript, formatter.isSubscript());
			setActive(superscript, formatter.isSuperscript());
			setActive(strikethrough, formatter.isStrikethrough());
		}
	}

	/**
	 * @param button
	 * @param active
	 */
	private void setActive(Button button, boolean active) {
		if (active != button.getStyleName().contains(ACTIVE_STYLE_NAME)) {
			toggle(button);
		}
	}

	public void setRichText(RichTextArea richText) {
		if (richTextKeyUpRegistration != null) {
			richTextKeyUpRegistration.removeHandler();
			richTextKeyUpRegistration = null;
		}

		if (richTextClickRegistration != null) {
			richTextClickRegistration.removeHandler();
			richTextClickRegistration = null;
		}

		this.richText = richText;

		if (this.richText != null) {
			this.formatter = richText.getFormatter();
			richTextKeyUpRegistration = this.richText.addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					Widget sender = (Widget) event.getSource();
					if (sender == RichTextToolbar.this.richText) {
						updateStatus();
					}
				}
			});
			
			richTextClickRegistration = this.richText.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					RichTextToolbar.this.onClick(event);
				}
			});
		} else {
			this.formatter = null;
		}

	}
}
