//
//  TermsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 *
 */
public class TermsPage extends Page {

	private static TermsPageUiBinder uiBinder = GWT.create(TermsPageUiBinder.class);

	interface TermsPageUiBinder extends UiBinder<Widget, TermsPage> {}

	public TermsPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		Window.scrollTo(0, 0);
	}

}
