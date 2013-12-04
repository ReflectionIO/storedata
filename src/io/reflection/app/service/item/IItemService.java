//  
//  IItemService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.item;

import java.util.List;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.shared.datatypes.Item;

import com.spacehopperstudios.service.IService;

public interface IItemService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Item getItem(Long id) throws DataAccessException;

	/**
	 * @param item
	 * @return
	 */
	public Item addItem(Item item) throws DataAccessException;

	/**
	 * @param item
	 * @return
	 */
	public Item updateItem(Item item) throws DataAccessException;

	/**
	 * @param item
	 */
	public void deleteItem(Item item) throws DataAccessException;

	/**
	 * @param externalId
	 * @return
	 */
	public Item getExternalIdItem(String externalId) throws DataAccessException;

	/**
	 * @param internalId
	 * @return
	 */
	public Item getInternalIdItem(String internalId) throws DataAccessException;

	/**
	 * @param itemIds
	 * @return
	 */
	public List<Item> getExternalIdItemBatch(List<String> itemIds) throws DataAccessException;

	/**
	 * @param items
	 * @return
	 */
	public Long addItemsBatch(List<Item> items) throws DataAccessException;

}