//
//  UserRolesProvider.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 17 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.dataprovider;

import io.reflection.app.datatypes.shared.Role;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class UserRolesProvider extends AsyncDataProvider<Role> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Role> display) {}

}
