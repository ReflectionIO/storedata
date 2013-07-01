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
public class Item extends DataType {

	@Index
	public String externalId;
	@Index
	public String internalId;

	public String name;
	public float price;

	@Index
	public String source;

	@Index
	public String type;

	public Date added;
	public String currency;

}
