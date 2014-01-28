//
//  GwtCanvasBasedCanvasFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.charts;

import com.googlecode.gchart.client.GChartCanvasFactory;
import com.googlecode.gchart.client.GChartCanvasLite;

/**
 * @author billy1380
 * 
 */
public final class GwtCanvasBasedCanvasFactory implements GChartCanvasFactory {
	public GChartCanvasLite create() {
		return new GwtCanvasBasedCanvasLite();
	}

}
