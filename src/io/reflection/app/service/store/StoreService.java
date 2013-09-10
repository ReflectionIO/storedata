//  
//  StoreService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.datatypes.Pager;
import io.reflection.app.api.datatypes.SortDirectionType;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Store;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.spacehopperstudios.utility.StringUtils;

final class StoreService implements IStoreService {
	public String getName() {
		return ServiceType.ServiceTypeStore.toString();
	}

	@Override
	public Store getStore(Long id) {
		Store store = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection storeConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeStore.toString());

		final String getStoreQuery = String.format("SELECT * FROM `store` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());

		try {
			storeConnection.connect();
			storeConnection.executeQuery(getStoreQuery);

			if (storeConnection.fetchNextRow()) {
				store = toStore(storeConnection);
			}
		} finally {
			if (storeConnection != null) {
				storeConnection.disconnect();
			}
		}
		return store;
	}

	/**
	 * To store
	 * 
	 * @param connection
	 * @return
	 */
	private Store toStore(Connection connection) {
		Store store = new Store();
		store.id = connection.getCurrentRowLong("id");
		store.created = connection.getCurrentRowDateTime("created");
		store.deleted = connection.getCurrentRowString("deleted");

		store.a3Code = stripslashes(connection.getCurrentRowString("a3code"));
		store.countries = Arrays.asList(connection.getCurrentRowString("countries").split(","));
		store.name = stripslashes(connection.getCurrentRowString("name"));
		store.url = stripslashes(connection.getCurrentRowString("url"));

		return store;
	}

	@Override
	public Store addStore(Store store) {
		// LATER Auto-generated method stub addStore
		throw new UnsupportedOperationException();
	}

	@Override
	public Store updateStore(Store store) {
		// LATER Auto-generated method stub updateStore
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteStore(Store store) {
		// LATER Auto-generated method stub deleteStore
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.store.IStoreService#getCountryStores(io.reflection.app.datatypes.Country, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Store> getCountryStores(Country country, Pager pager) {
		List<Store> stores = new ArrayList<Store>();

		String commaDelimitedStoreCodes = null;

		if (country.stores != null && country.stores.size() > 0) {
			commaDelimitedStoreCodes = StringUtils.join(country.stores, "','");
		}

		if (commaDelimitedStoreCodes != null && commaDelimitedStoreCodes.length() != 0) {
			String getCountryStoresQuery = String.format("SELECT * FROM `store` WHERE `a3code` IN ('%s') AND `deleted`='n' ORDER BY `%s` %s LIMIT %d, %d",
					commaDelimitedStoreCodes, pager.sortBy, pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "asc" : "desc",
					pager.start.longValue(), pager.count.longValue());

			Connection storeConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeStore.toString());

			try {
				storeConnection.connect();
				storeConnection.executeQuery(getCountryStoresQuery);

				while (storeConnection.fetchNextRow()) {
					Store store = toStore(storeConnection);

					if (store != null) {
						stores.add(store);
					}

				}
			} finally {
				if (storeConnection != null) {
					storeConnection.disconnect();
				}
			}
		}
		return stores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.store.IStoreService#getStores(io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Store> getStores(Pager pager) {
		List<Store> stores = new ArrayList<Store>();

		final String getStoresQuery = String.format("SELECT * FROM `store` ORDER BY `%s` %s LIMIT %d, %d", pager.sortBy, pager.sortDirection,
				pager.start.longValue(), pager.count.longValue());
		Connection storeConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeStore.toString());

		try {
			storeConnection.connect();
			storeConnection.executeQuery(getStoresQuery);

			while (storeConnection.fetchNextRow()) {
				Store store = toStore(storeConnection);

				if (store != null) {
					stores.add(store);
				}
			}
		} finally {
			if (storeConnection != null) {
				storeConnection.disconnect();
			}
		}

		return stores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.store.IStoreService#searchStores(java.lang.String, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Store> searchStores(String query, Pager pager) {
		// TODO Auto-generated method stub searchStores
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.store.IStoreService#getA3CodeStore(java.lang.String)
	 */
	@Override
	public Store getA3CodeStore(String a3Code) {
		Store store = null;

		final String getA3CodeQuery = String.format("SELECT * FROM `store` WHERE `a3code`='%s' AND `deleted`='n' LIMIT 1", addslashes(a3Code));
		Connection storeConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeStore.toString());

		try {
			storeConnection.connect();
			storeConnection.executeQuery(getA3CodeQuery);

			if (storeConnection.fetchNextRow()) {
				store = toStore(storeConnection);
			}

		} finally {
			if (storeConnection != null) {
				storeConnection.disconnect();
			}
		}

		return store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.store.IStoreService#getNameStore(java.lang.String)
	 */
	@Override
	public Store getNamedStore(String name) {
		// TODO Auto-generated method stub getNameStore
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.store.IStoreService#getStoresCount()
	 */
	@Override
	public long getStoresCount() {
		long count = 0;

		// LATER Auto-generated method stub getStoresCount

		return count;
	}

}