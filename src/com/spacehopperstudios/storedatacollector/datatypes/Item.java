/**
 * 
 */
package com.spacehopperstudios.storedatacollector.datatypes;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 *
 */
@Entity
@Cache
public class Item {

	public String name;
	public float price;
	public String source;
	@Id
	public String id;
	@Index
	public String externalId;
	@Index
	public String internalId;
	public String type;
	public Date added;
	public String currency;

}
