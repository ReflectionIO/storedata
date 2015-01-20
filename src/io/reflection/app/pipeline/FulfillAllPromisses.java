//
//  FulfillAllPromisses.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import java.util.Map;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

public final class FulfillAllPromisses extends Job1<Void, Map<String, Double>> {

	private static final long serialVersionUID = -2335419676158668911L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(Map<String, Double> dataAccountFetchSummary) throws Exception {

		return null;
	}

}