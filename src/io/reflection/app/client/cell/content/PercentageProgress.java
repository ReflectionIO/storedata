//
//  PercentageProgress.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell.content;

/**
 * @author billy1380
 *
 */
public class PercentageProgress implements Progress {

	private float part;

	/**
	 * 
	 */
	public PercentageProgress(float value) {
		part = value;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.client.cell.content.Progress#getPart()
	 */
	@Override
	public float getPart() {
		return part;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.client.cell.content.Progress#getTotal()
	 */
	@Override
	public float getTotal() {
		return 100.0f;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Float.toString(part) + "%";
	}
	
}
