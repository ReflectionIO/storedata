/**
 * 
 */
package com.spacehopperstudios.storedata.datatypes;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@EntitySubclass
@Cache
public class Rank extends DataType {

	@Id
	public Long id;

	@Index
	public int position;

	@Index
	public String itemId;

	@Index
	public String type;

	@Index
	public String country;

	@Index
	public Date date;

	@Index
	public String source;

	public float price;
	public String currency;

	/**
	 * This flag indicates whether this row has been counted
	 */
	@Index
	public boolean counted;

	public String code;
}
