/**
 * 
 */
package com.spacehopperstudios.storedata.datatypes;

import java.util.Date;

import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@EntitySubclass
public class FeedFetch extends DataType {

	public String country;
	
	public String data;

	@Index
	public Date date;

	@Index
	public boolean ingested;

	@Index
	public String store;

	public int part;

	@AlsoLoad("totalparts")
	public int totalParts;

	@Index
	public String type;

	public String code;

}
