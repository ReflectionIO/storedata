//
//  ProgressBarCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.client.cell.content.Progress;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class ProgressBarCell<T extends Progress> extends AbstractCell<T> {
	
	interface ProgressBarCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String volume, String volumeMax, String percentage, String text);
	}

	private static ProgressBarCellRenderer RENDERER = GWT.create(ProgressBarCellRenderer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb) {
		RENDERER.render(sb, Float.toString(value.getPart()), Float.toString(value.getTotal()), Float.toString((value.getPart() / value.getTotal()) * 100.0f), value.toString());
	}

}