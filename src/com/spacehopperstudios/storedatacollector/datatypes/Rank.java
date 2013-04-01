/**
 * 
 */
package com.spacehopperstudios.storedatacollector.datatypes;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * @author billy1380
 * 
 */
@Entity
@Cache
public class Rank {

	public int position;
	public String itemId;
	public String type;
	public String country;
	public Date date;
	@Id
	public Long id;
	public String source;

}
