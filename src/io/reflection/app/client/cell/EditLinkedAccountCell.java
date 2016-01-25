//
//  EditLinkedAccountCell.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 29 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.client.res.Styles;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class EditLinkedAccountCell extends AbstractCell<String> {

	interface Templates extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<a class=\"{0}\" style=\"cursor: pointer\">Edit</a>")
		SafeHtml cell(String styles, SafeHtml value);
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	private static Templates templates = GWT.create(Templates.class);

	public EditLinkedAccountCell() {
		super("click");
	}

	/**
	 * Called when an event occurs in a rendered instance of this Cell. The parent element refers to the element that contains the rendered cell, NOT to the
	 * outermost element that the Cell rendered.
	 */
	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		// Handle the click event.
		if ("click".equals(event.getType())) {
			Element clickedElem = Element.as(event.getEventTarget());
			if (parent.getFirstChildElement().isOrHasChild(clickedElem)) {
				valueUpdater.update(value); // Value is the data account id
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		/*
		 * Always do a null check on the value. Cell widgets can pass null to cells if the underlying data contains a null, or if the data arrives out of order.
		 */
		if (value == null) { return; }

		// If the value comes from the user, we escape it to avoid XSS attacks.
		SafeHtml safeValue = SafeHtmlUtils.fromString(value);

		// Use the template to create the Cell's html.
		String styles = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonFunctionSmall();
		SafeHtml rendered = templates.cell(styles, safeValue);
		sb.append(rendered);
	}

}
