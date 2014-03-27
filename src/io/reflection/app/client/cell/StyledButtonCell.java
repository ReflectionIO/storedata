//
//  StyledButtonCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 26 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import java.util.Arrays;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
public class StyledButtonCell extends ButtonCell {

	private String style;

	public StyledButtonCell(String... style) {
		super();

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
		if (style == null || style.length() == 0) {
			super.render(context, data, sb);
		} else {
			sb.appendHtmlConstant("<button type=\"button\" class=\"" + style + "\" tabindex=\"-1\">");
			if (data != null) {
				sb.append(data);
			}
			sb.appendHtmlConstant("</button>");
		}
	}

}
