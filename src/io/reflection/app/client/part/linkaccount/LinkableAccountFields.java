//
//  LinkableAccountFields.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part.linkaccount;

import com.google.gwt.user.client.ui.Focusable;

import io.reflection.app.client.handler.EnterPressedEventHandler;

/**
 * @author billy1380
 * 
 */
public interface LinkableAccountFields {
	boolean validate();

	void setFormErrors();

	String getAccountSourceName();

	Long getAccountSourceId();

	String getUsername();

	String getPassword();

	String getProperties();

	void setOnEnterPressed(EnterPressedEventHandler handler);

	Focusable getFirstToFocus();

}
