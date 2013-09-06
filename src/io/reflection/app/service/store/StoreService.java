//  
//  StoreService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.store;

import io.reflection.app.datatypes.Store;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class StoreService implements IStoreService {
	public String getName() {
		return ServiceType.ServiceTypeStore.toString();
	}

	@Override
	public Store getStore(Long id) {
		Store store = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection storeConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeStore.toString());

		String getStoreQuery = String.format("select * from `store` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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
		return store;
	}

	@Override
	public Store addStore(Store store) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Store updateStore(Store store) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteStore(Store store) {
		throw new UnsupportedOperationException();
	}

}