//  
//  Store.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.datatypes;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@Entity
public class Store extends DataType {

	/**
	 * Store 3 character alpha code
	 */
	@Index
	public String a3code;

	/**
	 * Store name
	 */
	@Index
	public String name;

	/**
	 * Store online url
	 */
	public String url;

	/**
	 * Comma delimited list of a2 country codes that the store operates in
	 */
	public String countries;

}
