//  
//  Country.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.datatypes;

import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 *         Loosely based on http://en.wikipedia.org/wiki/ISO_3166-1
 */
@EntitySubclass
public class Country extends DataType {

	/**
	 * Country name
	 */
	@Index
	public String name;

	/**
	 * Country 2 character alpha code
	 */
	@Index
	public String a2Code;

	/**
	 * Country 3 alpha character code
	 */
	public String a3Code;

	/**
	 * Country numeric code
	 */
	public int nCode;

	/**
	 * Comma delimited lists of a3 store codes of stores in that country
	 */
	public String stores;

}
