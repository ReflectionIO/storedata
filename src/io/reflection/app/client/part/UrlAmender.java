//
//  UrlAmender.java
//  storedata
//
//  Created by Stefano Capuzzi on 20 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

/**
 * @author Stefano Capuzzi
 *
 */
public interface UrlAmender {

	void setName(String name);

	String getName();

	void setProperty(String key, Object value);

	Object getProperty(String keys);

	void updateFromString(String encoded);

	String asUrlAmenderString();

}
