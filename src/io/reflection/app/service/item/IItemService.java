//  
//  IItemService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.item;

import io.reflection.app.datatypes.Item;

import com.spacehopperstudios.service.IService;

public interface IItemService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Item getItem(Long id);

	/**
	 * @param item
	 * @return
	 */
	public Item addItem(Item item);

	/**
	 * @param item
	 * @return
	 */
	public Item updateItem(Item item);

	/**
	 * @param item
	 */
	public void deleteItem(Item item);

	/**
	 * @param externalId
	 * @return
	 */
	public Item getExternalIdItem(String externalId);

	/**
	 * @param internalId
	 * @return
	 */
	public Item getInternalIdItem(String internalId);

}