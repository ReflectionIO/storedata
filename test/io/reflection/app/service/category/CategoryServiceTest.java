//
//  CategoryServiceTest.java
//  storedata
//
//  Created by William Shakour (billy1380) on 31 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.service.category;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.test.DatabaseTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author billy1380
 * 
 */
public class CategoryServiceTest extends DatabaseTest {

	@Test
	public void testGetAllStoreCategories() throws DataAccessException {
		setupDatabaseConnectionSystemProperties();

		Store store = StoreServiceProvider.provide().getA3CodeStore("ios");

		Long categoryCount = CategoryServiceProvider.provide().getStoreCategoriesCount(store);

		Assert.assertNotEquals(categoryCount.longValue(), 0);

		Pager p = new Pager();
		p.start = Long.valueOf(0);
		p.count = categoryCount;

		List<Category> category = CategoryServiceProvider.provide().getStoreCategories(store, p);
		
		Assert.assertEquals(category.size(), 24);
		
	}
	
	@Test
	public void testGetExcludingAllStoreCategories() throws DataAccessException {
		setupDatabaseConnectionSystemProperties();
		
		Store store = StoreServiceProvider.provide().getA3CodeStore("ios");
		
		Category all = CategoryServiceProvider.provide().getAllCategory(store);
		
		Assert.assertNotNull(all);
		
		Long categoryCount = CategoryServiceProvider.provide().getParentCategoriesCount(all);
		
		Assert.assertNotEquals(categoryCount.longValue(), 0);
		
		Pager p = new Pager();
		p.start = Long.valueOf(0);
		p.count = categoryCount;
		
		List<Category> category = CategoryServiceProvider.provide().getParentCategories(all, p);
		
		Assert.assertEquals(category.size(), 23);
	}
	


}
