//
//  ImageAndTextCell.java
//  storedata
//
//  Created by William Shakour (billy1380) on 12 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell;

import io.reflection.app.client.cell.content.ImageAndText;
import io.reflection.app.client.res.Images;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * @author billy1380
 * 
 */
public class ImageAndTextCell<T extends ImageAndText> extends AbstractCell<T> {

	interface ImageAndTextCellRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, SafeHtml text, String image);
	}

	private static ImageAndTextCellRenderer RENDERER = GWT.create(ImageAndTextCellRenderer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb) {
	    SafeHtml text = value.getText();
	    String image = value.getImageStyleName();
	    if (image == "" || text == SafeHtmlUtils.fromSafeConstant("")){
	        text = SafeHtmlUtils.fromSafeConstant("<img src=\""+Images.INSTANCE.spinnerLoader().getSafeUri().asString()+"\"/>");
	        image = "";
	    }
		RENDERER.render(sb, text, image);
	}

}
