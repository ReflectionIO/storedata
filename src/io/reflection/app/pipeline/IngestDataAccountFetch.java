//
//  IngestDataAccountFetch.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

public class IngestDataAccountFetch extends Job1<Void, Long> {

	private static final long serialVersionUID = 3258834001291433964L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(Long dataAccountFetchId) throws Exception {

		return null;
	}

}