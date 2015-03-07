//
//  GatherFeed.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.collectors.CollectorIOS;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;

public class GatherFeed extends Job4<Long, String, String, Long, Long> {

	private static final long serialVersionUID = 959780630540839671L;

	private String name = null;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job4#run(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Long> run(String countryCode, String listName, Long categoryInternalId, Long code) throws Exception {
		// we only care about the first id since we no longer attempt to store the feed in the feedfetch table (and even if we did we would only need the
		// first id)

		return immediate(new CollectorIOS().collect(countryCode, listName, categoryInternalId == null ? null : Long.toString(categoryInternalId), code).get(0));
	}

	public GatherFeed name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return (name == null ? super.getJobDisplayName() : name);
	}
}