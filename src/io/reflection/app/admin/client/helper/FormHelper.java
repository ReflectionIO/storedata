//
//  FormHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.helper;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author billy1380
 * 
 */
public class FormHelper {
	public static void showNote(boolean isError, HTMLPanel group, HTMLPanel note, String text) {
		if (group != null) {
			if (isError) {
				group.addStyleName("alert alert-danger has-error");
			} else {
				group.addStyleName("alert alert-success has-success");
			}
		}

		if (note != null) {
			note.setVisible(true);
			note.getElement().setInnerText(text);
		}
	}

	public static void hideNote(HTMLPanel group, HTMLPanel note) {
		if (note != null) {
			note.setVisible(false);
		}

		if (group != null) {
			group.removeStyleName("alert alert-danger alert-success has-error");
		}
	}
}
