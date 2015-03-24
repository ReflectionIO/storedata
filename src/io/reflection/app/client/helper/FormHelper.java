//
//  FormHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.component.FormFieldSelect;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class FormHelper {

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final RegExp REG_EXP_EMAIL_CHECKER = RegExp.compile(EMAIL_PATTERN);

	public static final String TRIM_PATTERN = "^[ \t]+|[ \t]+$";
	private static final RegExp REG_EXP_TRIM_CHECKER = RegExp.compile(TRIM_PATTERN);

	private static final String APPLE_VENDOR_ID_PATTERN = "^8[0-9]{7}$"; // 8 followed by seven numbers of any value
	private static final RegExp REG_EXP_APPLE_VENDOR_ID_CHECKER = RegExp.compile(FormHelper.APPLE_VENDOR_ID_PATTERN);

	public static final String COMPLETE_ACTION_NAME = "complete";
	public static final String REQUEST_INVITE_ACTION_NAME = "requestinvite";
	public static final int CODE_PARAMETER_INDEX = 1;

	public static final String HAS_MIXCASE = "(?=.*[a-z])(?=.*[A-Z])";
	private static final RegExp REG_EXP_HAS_MIXCASE_CHECKER = RegExp.compile(HAS_MIXCASE);
	public static final String HAS_SPECIALCASE = "(?=.*[!@#$&*])";
	private static final RegExp REG_EXP_HAS_SPECIALCASE_CHECKER = RegExp.compile(HAS_SPECIALCASE);
	public static final String HAS_DIGIT = "(?=.*[0-9])";
	private static final RegExp REG_EXP_HAS_DIGIT_CHECKER = RegExp.compile(HAS_DIGIT);

	public static boolean isValidEmail(String toValidate) {
		return REG_EXP_EMAIL_CHECKER.test(toValidate);
	}

	public static boolean isTrimmed(String toValidate) {
		return !REG_EXP_TRIM_CHECKER.test(toValidate);
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
	 * @param FormFieldSelect
	 * @param value
	 * @return
	 */
	public static int getItemIndex(FormFieldSelect listBox, String value) {
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

	/**
	 * Return password strength indicator from 0 (weak) to 3
	 * 
	 * @param toValidate
	 * @return
	 */
	public static int getPasswordStrength(String toValidate) {
		int strength = 0;
		if (toValidate.length() >= 6) {
			if (REG_EXP_HAS_MIXCASE_CHECKER.test(toValidate)) {
				strength++;
			}
			if (REG_EXP_HAS_SPECIALCASE_CHECKER.test(toValidate)) {
				strength++;
			}
			if (REG_EXP_HAS_DIGIT_CHECKER.test(toValidate)) {
				strength++;
			}
			// additional rule could be length > 12
		}
		return strength;
	}
}
