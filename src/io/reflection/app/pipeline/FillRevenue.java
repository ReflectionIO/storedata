//
//  FillRevenue.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class FillRevenue extends Job2<Void, Long, Long> {

	private static final long serialVersionUID = -8880236066719929555L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Void> run(Long feedFetchId, Long simpleModelRunId) throws Exception {

		return null;
	}

}
