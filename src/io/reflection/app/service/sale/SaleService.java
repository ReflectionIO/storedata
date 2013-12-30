//  
//  SaleService.java
//  reflection.io
//
//  Created by William Shakour on December 30, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.sale;

import java.util.List;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.DataAccount;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Sale;

final class SaleService implements ISaleService {
	public String getName() {
		return ServiceType.ServiceTypeSale.toString();
	}

	@Override
	public Sale getSale(Long id) throws DataAccessException {
		Sale sale = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection saleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeSale.toString());

		String getSaleQuery = String.format("select * from `sale` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			saleConnection.connect();
			saleConnection.executeQuery(getSaleQuery);

			if (saleConnection.fetchNextRow()) {
				sale = toSale(saleConnection);
			}
		} finally {
			if (saleConnection != null) {
				saleConnection.disconnect();
			}
		}
		return sale;
	}

	/**
	 * To sale
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private Sale toSale(Connection connection) throws DataAccessException {
		Sale sale = new Sale();
		sale.id = connection.getCurrentRowLong("id");
		return sale;
	}

	@Override
	public Sale addSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Sale updateSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSale(Sale sale) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountItems(io.reflection.app.shared.datatypes.DataAccount, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Item> getDataAccountItems(DataAccount linkedAccount, Pager pager) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.sale.ISaleService#getDataAccountItemsCount()
	 */
	@Override
	public Long getDataAccountItemsCount() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

}