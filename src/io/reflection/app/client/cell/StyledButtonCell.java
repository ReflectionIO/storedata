//
//  StyledButtonCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 26 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

import java.util.Arrays;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
public class StyledButtonCell extends AbstractSafeHtmlCell<String> {

	private String style;

	public StyledButtonCell(String... style) {
		this();

		if (style != null && style.length > 0) {
			this.style = StringUtils.join(Arrays.asList(style), " ");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.ButtonCell#render(com.google.gwt.cell.client.Cell.Context, com.google.gwt.safehtml.shared.SafeHtml,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
		String stylePart = (style == null || style.length() == 0) ? "" : "class=\"" + style + "\"";
		sb.appendHtmlConstant("<button type=\"button\" " + stylePart + " tabindex=\"-1\">");
		if (data != null) {
			sb.append(data);
		}
		sb.appendHtmlConstant("</button>");
	}

	/**
	 * Construct a new ButtonCell that will use a {@link SimpleSafeHtmlRenderer}.
	 */
	public StyledButtonCell() {
		this(SimpleSafeHtmlRenderer.getInstance());
	}

	/**
	 * Construct a new ButtonCell that will use a given {@link SafeHtmlRenderer}.
	 * 
	 * @param renderer
	 *            a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
	 */
	public StyledButtonCell(SafeHtmlRenderer<String> renderer) {
		super(renderer, CLICK, KEYDOWN);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if (BrowserEvents.CLICK.equals(event.getType())) {
			EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) { return; }
			if (parent.hasChildNodes() && parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
				onEnterKeyDown(context, parent, value, event, valueUpdater);
			}
		}
	}

	@Override
	protected void onEnterKeyDown(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		if (valueUpdater != null) {
			valueUpdater.update(value);
		}
	}

}
