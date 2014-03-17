//
//  ServiceCreator.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.blog.client.BlogService;
import io.reflection.app.api.core.client.CoreService;

/**
 * @author billy1380
 * 
 */
public class ServiceCreator implements ServiceConstants {

	public static CoreService createCoreService() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);
		service.setBus(EventController.get());
		return service;
	}

	public static AdminService createAdminService() {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);
		service.setBus(EventController.get());
		return service;
	}

	public static BlogService createBlogService() {
		BlogService service = new BlogService();
		service.setUrl(BLOG_END_POINT);
		service.setBus(EventController.get());
		return service;
	}

	// public static LookupService createLookupService() {
	// LookupService service = new LookupService();
	// service.setUrl(LOOKUP_END_POINT);
	// service.setBus(EventController.get());
	// return service;
	// }

}
