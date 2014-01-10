//
//  Breadcrumbs.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class Breadcrumbs extends Composite {

	private static BreadcrumbsUiBinder uiBinder = GWT.create(BreadcrumbsUiBinder.class);

	interface BreadcrumbsUiBinder extends UiBinder<Widget, Breadcrumbs> {}

	@UiField OListElement mList;

	public Breadcrumbs() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void push(String... some) {

		for (int i = 0; i < some.length; i++) {
			LIElement e = Document.get().createLIElement();

			e.setInnerText(some[i]);

			mList.appendChild(e);
		}
	}

	public void pop() {
		mList.getLastChild().removeFromParent();
	}

	public void pushAll(List<String> all) {
		for (String one : all) {
			push(one);
		}
	}

	public void setPath(String path) {
		String[] pathSplit = path.split("/");
		pushAll(Arrays.asList(pathSplit));
	}

	/**
	 * 
	 */
	public void clear() {
		while (mList.hasChildNodes()) {
			mList.removeChild(mList.getLastChild());
		}
	}

}
