//
//  AlertBoxHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.helper;

import io.reflection.app.admin.client.part.AlertBox;
import io.reflection.app.admin.client.part.AlertBox.AlertBoxType;

/**
 * @author billy1380
 * 
 */
public class AlertBoxHelper {

	public static AlertBox configureAlert(AlertBox a, AlertBoxType type, boolean isLoading, String text, String detail, boolean canDismiss) {

		a.setType(type);
		a.setLoading(isLoading);
		a.setText(text);
		a.setDetail(detail);
		a.setCanDismiss(canDismiss);

		return a;
	}

}
