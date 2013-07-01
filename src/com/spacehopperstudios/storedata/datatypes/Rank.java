/**
 * 
 */
package com.spacehopperstudios.storedata.datatypes;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@Entity
@Cache
public class Rank extends DataType {

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
