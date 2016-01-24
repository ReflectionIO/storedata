//
//  RanksGroup.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import io.reflection.app.datatypes.shared.Rank;

/**
 * @author billy1380
 * 
 */
public class RanksGroup {
	public Rank free;
	public Rank paid;
	public Rank grossing;

	public static RanksGroup getPlaceholder() {
		RanksGroup rg = new RanksGroup();
		rg.free = new Rank();
		rg.grossing = new Rank();
		rg.paid = new Rank();
		return rg;
	}
}
