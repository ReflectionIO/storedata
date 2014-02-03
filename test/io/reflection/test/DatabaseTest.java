//
//  DatabaseTest.java
//  storedata
//
//  Created by William Shakour (billy1380) on 31 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.test;

import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;

/**
 * @author billy1380
 * 
 */
public class DatabaseTest {

	protected void setupLiveDatabaseConnectionSystemProperties() {
		System.setProperty("connection.native", "true");
		System.setProperty(IDatabaseService.DATABASE_SERVER_KEY, "173.194.104.108");
		System.setProperty(IDatabaseService.DATABASE_CATALOGURE_KEY, "rio");
		System.setProperty(IDatabaseService.DATABASE_USERNAME_KEY, "rio_app_user");
		System.setProperty(IDatabaseService.DATABASE_PASSWORD_KEY, "sooth28@duns");
	}

	protected void setupLocalDatabaseConnectionSystemProperties() {
		System.setProperty("connection.native", "true");
		System.setProperty(IDatabaseService.DATABASE_SERVER_KEY, "127.0.0.1");
		System.setProperty(IDatabaseService.DATABASE_CATALOGURE_KEY, "rio");
		System.setProperty(IDatabaseService.DATABASE_USERNAME_KEY, "rio_app_user");
		System.setProperty(IDatabaseService.DATABASE_PASSWORD_KEY, "");
	}

}
