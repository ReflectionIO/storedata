//
//  ReadyToStartPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 11 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 *
 */
public class ReadyToStartPage extends Composite {

	private static ReadyToStartPageUiBinder uiBinder = GWT.create(ReadyToStartPageUiBinder.class);

	interface ReadyToStartPageUiBinder extends UiBinder<Widget, ReadyToStartPage> {}

	
	public ReadyToStartPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
