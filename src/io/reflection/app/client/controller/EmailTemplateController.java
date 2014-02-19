//
//  EmailTemplateController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.datatypes.shared.EmailTemplate;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * @author billy1380
 * 
 */
public class EmailTemplateController extends AsyncDataProvider<EmailTemplate> implements ServiceController {

	public void fetchEmailTemplates() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<EmailTemplate> display) {

	}

}
