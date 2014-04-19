//  
//  ISaleService.java
//  reflection.io
//
//  Created by William Shakour on December 30, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.sale;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
	public List<Item> getDataAccountItems(DataAccount dataAccount, Pager pager) throws DataAccessException;

	/**
	 * @return
	 */
	public Long getDataAccountItemsCount(DataAccount dataAccount) throws DataAccessException;

	/**
	 * @param sales
	 * @return
	 * @throws DataAccessException
	 */
	public Long addSalesBatch(Collection<Sale> sales) throws DataAccessException;

	/**
	 * @param country
	 * @param category
	 * @param linkedAccount
	 * @param start
	 * @param end
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Sale> getSales(Country country, Category category, DataAccount linkedAccount, Date start, Date end, Pager pager) throws DataAccessException;

	public Long getSalesCount(Country country, Category category, DataAccount linkedAccount, Date start, Date end) throws DataAccessException;

}