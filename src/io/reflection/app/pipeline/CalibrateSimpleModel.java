//
//  CalibrateSimpleModel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import java.util.Map;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrateSimpleModel extends Job3<Long, String, Map<String, Double>, Long> {

	private static final long serialVersionUID = -8764419384476424579L;

	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job3#run(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Value<Long> run(String type, Map<String, Double> summary, Long feedFetchId) throws Exception {
		return null;
	}

}
