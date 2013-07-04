//
//  InputValidationException.java
//  jspacecloud
//
//  Created by William Shakour on Jun 15, 2012.
//  Copyright © 2012 Spacehopper Studios Ltd. All rights reserved.
//
package com.willshex.gson.json.service;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class InputValidationException extends Exception {
	protected int code;

	public int getCode() {
		return code;
	}

	public InputValidationException(int code, String message) {
		super(message);
		this.code = code;
	}

	public String toString() {
		return "Code:" + String.valueOf(code) + " = " + getClass().getSimpleName() + ": " + getMessage();
	}
}
