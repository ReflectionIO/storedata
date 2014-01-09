//  
//  ISaleService.java
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
import io.reflection.app.shared.datatypes.DataAccount;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Sale;

import com.spacehopperstudios.service.IService;

public interface ISaleService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Sale getSale(Long id) throws DataAccessException;

	/**
	 * @param sale
	 * @return
	 */
	public Sale addSale(Sale sale) throws DataAccessException;

	/**
	 * @param sale
	 * @return
	 */
	public Sale updateSale(Sale sale) throws DataAccessException;

	/**
	 * @param sale
	 */
	public void deleteSale(Sale sale) throws DataAccessException;

	/**
	 * @param linkedAccount
	 * @param pager
	 * @return
	 */
	public List<Item> getDataAccountItems(DataAccount linkedAccount, Pager pager) throws DataAccessException;

	/**
	 * @return
	 */
	public Long getDataAccountItemsCount() throws DataAccessException;

}