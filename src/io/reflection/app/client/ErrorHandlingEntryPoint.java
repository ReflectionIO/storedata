//
//  ErrorHandlingEntryPoint.java
//  repackaged
//
//  Created by billy1380 on 4 Aug 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS Ltd. All rights reserved.
//  
//

package io.reflection.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;

/**
 * @author billy1380
 * 
 */
public abstract class ErrorHandlingEntryPoint implements EntryPoint {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		handleUncaughtExceptions();
	}

	private void handleUncaughtExceptions() {
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable e) {
				Window.alert(messageForThrowable(e));
			}

			private String messageForThrowable(Throwable e) {
				String message = "";
				if (e.getCause() != null) {
					message += "Caused by: " + messageForThrowable(e.getCause());
				}

				message += e.getMessage() + "\n";

				message += "-- Frames --\n";

				for (StackTraceElement frame : e.getStackTrace()) {
					message += frame.getFileName() + ": " + frame.getClassName() + frame.getMethodName() + " on line " + frame.getLineNumber() + "\n";
				}

				return message;
			}
		});
	}

}
