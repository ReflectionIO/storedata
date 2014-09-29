//
//  Modeller.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.modellers;

import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ModelTypeType;

/**
 * @author billy1380
 * 
 */
public interface Modeller {

	void enqueue(ModelTypeType modelType, String country, Category category, String listType, Long code);
	
	void enqueue(FeedFetch feedFetch);
	
	void enqueue(FeedFetch free, FeedFetch paid, FeedFetch grossing);

	FormType getForm(String type);

	String getGrossingType(FormType formType);

	String getType(FormType formType, Boolean isFree);

}
