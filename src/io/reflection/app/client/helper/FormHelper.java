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
import com.google.gwt.user.client.ui.ListBox;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class FormHelper {

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final RegExp REG_EXP_EMAIL_CHECKER = RegExp.compile(FormHelper.EMAIL_PATTERN);

	private static final String APPLE_VENDOR_ID_PATTERN = "^8[0-9]{7}$"; // 8 followed by seven numbers of any value
	private static final RegExp REG_EXP_APPLE_VENDOR_ID_CHECKER = RegExp.compile(FormHelper.APPLE_VENDOR_ID_PATTERN);

	public static final String COMPLETE_ACTION_NAME = "complete";
	public static final int CODE_PARAMETER_INDEX = 1;

	public static boolean isValidEmail(String toValidate) {
		return REG_EXP_EMAIL_CHECKER.test(toValidate);
	}

	public static boolean isValidAppleVendorId(String toValidate) {
		return REG_EXP_APPLE_VENDOR_ID_CHECKER.test(toValidate);
	}

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

	/**
	 * @param listBox
	 * @param value
	 * @return
	 */
	public static int getItemIndex(ListBox listBox, String value) {
		int index = -1;
		int count = listBox.getItemCount();

		for (int i = 0; i < count; i++) {
			if (listBox.getValue(i).equals(value)) {
				index = i;

				break;
			}
		}

		return index;
	}

}
