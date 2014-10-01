//
//  ArchivableKeyValue.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivablekeyvalue.peristence.objectify;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * @author billy1380
 * 
 */
@Entity
@Cache
public class ArchivableKeyValue {
	public @Id String key;
	public String value;
}
