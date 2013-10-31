//
//  Modeller.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.modellers;

import io.reflection.app.shared.datatypes.FormType;

/**
 * @author billy1380
 * 
 */
public interface Modeller {

	void enqueue(String store, String country, String type, String code);

	void modelVariables(String store, String country, String type, String code);

	FormType getForm(String type);

}
