//
//  DeleteLinkedAccount.java
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
public class DeleteLinkedAccountCell extends AbstractCell<String> {

	interface Templates extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<a class=\"{0}\" style=\"cursor: pointer\"><svg version=\"1.1\" x=\"0px\" y=\"0px\" width=\"24.1px\" height=\"32px\" viewBox=\"0 0 24.1 32\" enable-background=\"new 0 0 24.1 32\" xml:space=\"preserve\"><defs></defs><g><defs><rect id=\"SVGID_1_\" x=\"0\" y=\"0\" width=\"24.1\" height=\"32\"></rect></defs><clipPath id=\"SVGID_2_\"><use xlink:href=\"#SVGID_1_\" overflow=\"visible\"></use></clipPath><path clip-path=\"url(#SVGID_2_)\" fill=\"#81879d\" d=\"M22.4,5H12H1.7C-0.2,5,0,8.7,0,8.7h12h12C24.1,8.7,24.3,5,22.4,5\"></path><path clip-path=\"url(#SVGID_2_)\" fill=\"#81879d\" d=\"M12,9.7H1v19.1C1,31.1,2.9,32,4.7,32H12h7.3c1.8,0,3.6-0.8,3.6-3.2V9.7H12zM6,28.5c0,0.4-0.4,0.8-0.9,0.8s-0.9-0.4-0.9-0.8V13.2c0-0.4,0.4-0.8,0.9-0.8S6,12.7,6,13.2V28.5z M10.6,28.5c0,0.4-0.4,0.8-0.9,0.8c-0.5,0-0.9-0.4-0.9-0.8V13.2c0-0.4,0.4-0.8,0.9-0.8c0.5,0,0.9,0.4,0.9,0.8V28.5z M15.3,28.5c0,0.4-0.4,0.8-0.9,0.8c-0.5,0-0.9-0.4-0.9-0.8V13.2c0-0.4,0.4-0.8,0.9-0.8c0.5,0,0.9,0.4,0.9,0.8V28.5z M20,28.5c0,0.4-0.4,0.8-0.9,0.8s-0.9-0.4-0.9-0.8V13.2c0-0.4,0.4-0.8,0.9-0.8s0.9,0.4,0.9,0.8V28.5z\"></path><path clip-path=\"url(#SVGID_2_)\" fill=\"#81879d\" d=\"M12,0C6.9,0,5.9,4.3,5.9,4.3h1.5c0,0,0.8-3.1,4.7-3.1c3.9,0,4.7,3.1,4.7,3.1h1.5C18.2,4.3,17.2,0,12,0\"></path></g></svg></a>")
		SafeHtml cell(String styles, SafeHtml value);
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	private static Templates templates = GWT.create(Templates.class);

	public DeleteLinkedAccountCell() {
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
		String styles = Styles.STYLES_INSTANCE.reflectionMainStyle().deleteAccountLink();
		SafeHtml rendered = templates.cell(styles, safeValue);
		sb.append(rendered);
	}

}
