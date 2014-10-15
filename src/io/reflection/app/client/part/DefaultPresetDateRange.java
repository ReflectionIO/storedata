//
//  DefaultPresetDateRange.java
//  storedata
//
//  Created by Stefano Capuzzi on 15 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.part.datatypes.DateRange;

/**
 * @author Stefano Capuzzi
 * 
 */
public class DefaultPresetDateRange {

	private String name;
	private DateRange dateRange;

	public DefaultPresetDateRange(String name, DateRange dateRange) {
		this.name = name;
		this.dateRange = dateRange;
	}

	public String getName() {
		return name;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

}
