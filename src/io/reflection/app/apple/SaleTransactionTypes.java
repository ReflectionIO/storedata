//
//  SaleTransactionTypes.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.apple;

/**
 * @author William Shakour (billy1380)
 *
 */
public interface SaleTransactionTypes {
	// iPhone
	public static final String FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS = "1";
	public static final String UPDATE_IPHONE_AND_IPOD_TOUCH_IOS = "7";

	// Universal
	public static final String FREE_OR_PAID_APP_UNIVERSAL_IOS = "1F";
	public static final String UPDATE_UNIVERSAL_IOS = "7F";

	// iPad
	public static final String FREE_OR_PAID_APP_IPAD_IOS = "1T";
	public static final String UPDATE_IPAD_IOS = "7T";

	// IAP
	public static final String INAPP_PURCHASE_PURCHASE_IOS = "IA1";
	// Subscription
	public static final String INAPP_PURCHASE_SUBSCRIPTION_IOS = "IA9";
	// Auto-renewable subscription
	public static final String INAPP_PURCHASE_AUTO_RENEWABLE_SUBSCRIPTION_IOS = "IAY";
	// Free subscription
	public static final String INAPP_PURCHASE_FREE_SUBSCRIPTION_IOS = "IAC";

	// Mac App - TO IGNORE
	public static final String FREE_OR_PAID_APP_MAC_APP = "F1";
	public static final String UPDATE_MAC_APP = "F7";
	public static final String INAPP_PURCHASE_MAC_APP = "FI1";

	// Custom
	public static final String PAID_APP_CUSTOM_IPHONE_AND_IPOD_TOUCH_IOS = "1E";
	public static final String PAID_APP_CUSTOM_IPAD_IOS = "1EP";
	public static final String PAID_APP_CUSTOM_UNIVERSAL_IOS = "1EU";
}
