//
//  FormHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.component.Selector;

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

	public static final String ERROR_FIRST_NAME_EMPTY = "Please tell us your first name";
	public static final String ERROR_LAST_NAME_EMPTY = "Please tell us your last name";
	public static final String ERROR_NAME_SHORT = "Name must be a minimum of 2 characters long";
	public static final String ERROR_EMAIL_EMPTY = "Please enter your email address";
	public static final String ERROR_EMAIL_WRONG = "Email address is invalid, please check what you've entered";
	public static final String ERROR_EMAIL_MISSING_AT = "Invalid email address: @ symbol is missing";
	// public static final String ERROR_EMAIL_INVALID_EXTENSION = ""; TODO ?
	public static final String ERROR_EMAIL_LOGIN_EMPTY = "We can't let you in without your email address";
	public static final String ERROR_COMPANY_EMPTY = "Please tell us your company name";
	public static final String ERROR_COMPANY_SHORT = "Company must be a minimum of 2 characters long";
	public static final String ERROR_PASSWORD_CREATE_EMPTY = "Very trusting of you but we need a password for security";
	public static final String ERROR_PASSWORD_CREATE_SHORT = "Passwords must be at least 6 characters (you can use any characters though)";
	public static final String ERROR_PASSWORD_CREATE_CONFIRMATION_MATCH = "Oops..the passwords you've entered don't match";
	public static final String ERROR_PASSWORD_LOGIN_EMPTY = "We can't let you in without your password";
	public static final String ERROR_PASSWORD_LOGIN_WRONG = "Hang on...that's not your password, please try again";
	public static final String ERROR_LOGIN_CREDENTIALS_WRONG = "Hang on...the email or password you entered is not right, please try again";
	public static final String ERROR_BUTTON_INCOMPLETE = "Oops, Something's Missing";
	public static final String ERROR_BUTTON_WRONG = "Oops, Something's Wrong";
	public static final String ERROR_FORM_EMPTY_FIELDS = "Please complete the highlighted fields above you may have missed";
	public static final String ERROR_FORM_WRONG_FIELDS = "Please check the highlighted fields above for errors";
	public static final String ERROR_FORM_GENERIC = "Please check the information above for errors and try again";
	public static final String ERROR_LINKEDACCOUNT_NAME_EMPTY = "We need your app store account name in order to link it";
	public static final String ERROR_LINKEDACCOUNT_PASSWORD_EMPTY = "We need your app store account password in order to link it";
	public static final String ERROR_LINKEDACCOUNT_UPDATE_PASSWORD_EMPTY = "We need your app store account password in order to update it";
	public static final String ERROR_VENDORID_EMPTY = "We need your Apple Vendor ID please, you can find it here";
	public static final String ERROR_VENDORID_WRONG = "The vendor ID you entered is invalid, it must start with 8 and be 8 characters long";

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
	 * @param Selector
	 * @param value
	 * @return
	 */
	public static int getItemIndex(Selector listBox, String value) {
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
	 * Return password strength indicator from 0 (weak) to 4
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
			if (toValidate.length() > 12) {
				strength++;
			}
		}
		return strength;
	}
}
