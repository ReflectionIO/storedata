//  
//  ItemService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.item;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.datatypes.Item;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class ItemService implements IItemService {
	public String getName() {
		return ServiceType.ServiceTypeItem.toString();
	}

	@Override
	public Item getItem(Long id) {
		Item item = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection itemConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		String getItemQuery = String.format("select * from `item` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			itemConnection.connect();
			itemConnection.executeQuery(getItemQuery);

			if (itemConnection.fetchNextRow()) {
				item = toItem(itemConnection);
			}
		} finally {
			if (itemConnection != null) {
				itemConnection.disconnect();
			}
		}

		return item;
	}

	/**
	 * To item
	 * 
	 * @param connection
	 * @return
	 */
	private Item toItem(Connection connection) {
		Item item = new Item();

		item.id = connection.getCurrentRowLong("id");
		item.created = connection.getCurrentRowDateTime("created");
		item.deleted = connection.getCurrentRowString("deleted");

		item.added = connection.getCurrentRowDateTime("added");
		item.creatorName = connection.getCurrentRowString("creatorname");
		item.currency = connection.getCurrentRowString("currency");
		item.externalId = connection.getCurrentRowString("externalid");
		item.internalId = connection.getCurrentRowString("internalid");
		item.name = connection.getCurrentRowString("name");
		item.price = Float.valueOf((float) connection.getCurrentRowInteger("price").intValue() / 100.0f);
		item.properties = connection.getCurrentRowString("properties");
		item.source = connection.getCurrentRowString("source");
		item.type = connection.getCurrentRowString("type");

		return item;
	}

	@Override
	public Item addItem(Item item) {
		Item addedItem = null;

		String query = String
				.format("INSERT INTO `item` (`externalid`,`internalid`,`name`,`creatorname`,`price`,`source`,`type`,`added`,`currency`,`properties`) VALUES ('%s','%s','%s','%s',%d,'%s','%s','%s',%d,'%s')",
						addslashes(item.externalId), addslashes(item.internalId), addslashes(item.name), addslashes(item.creatorName), item.price,
						addslashes(item.source), addslashes(item.type), item.added.getTime(), addslashes(item.properties));

		Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		try {
			itemConnection.connect();
			itemConnection.executeQuery(query);

			if (itemConnection.getAffectedRowCount() > 0) {
				addedItem = getItem(Long.valueOf(itemConnection.getInsertedId()));
			}
		} finally {
			if (itemConnection != null) {
				itemConnection.disconnect();
			}
		}

		return addedItem;
	}

	@Override
	public Item updateItem(Item item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteItem(Item item) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.item.IItemService#getExternalIdItem(java.lang.String)
	 */
	@Override
	public Item getExternalIdItem(String externalId) {
		Item item = null;

		String query = String.format("SELECT * FROM `item` WHERE `externalid` = '%s' and `deleted`='n'", addslashes(externalId));
		Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		try {
			itemConnection.connect();
			itemConnection.executeQuery(query);

			if (itemConnection.fetchNextRow()) {
				item = toItem(itemConnection);
			}
		} finally {
			if (itemConnection != null) {
				itemConnection.disconnect();
			}
		}

		return item;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.item.IItemService#getInternalIdItem(java.lang.String)
	 */
	@Override
	public Item getInternalIdItem(String internalId) {
		// TODO Auto-generated method stub getInternalIdItem
		return null;
	}

}