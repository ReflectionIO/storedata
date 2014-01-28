//
//  FormHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class FormHelper {

	private static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static RegExp regExpEmailChecker = RegExp.compile(FormHelper.EMAIL_PATTERN);
	
	public static void showNote(boolean isError, HTMLPanel group, HTMLPanel note, String text) {
		if (group != null) {
			if (isError) {
				group.addStyleName("alert");
				group.addStyleName("alert-danger");
				group.addStyleName("has-error");
			} else {
				group.addStyleName("alert");
				group.addStyleName("alert-success");
				group.addStyleName("has-error");
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
			group.removeStyleName("alert");
			group.removeStyleName("alert-success");
			group.removeStyleName("alert-danger");
			group.removeStyleName("has-error");
		}
	}

	/**
	 * @param caught
	 * @return
	 */
	public static Error convertToError(Throwable caught) {
		Error e = new Error();
		e.code = Integer.valueOf(1000);
		e.message = caught.getMessage();

		return e;
	}
}
