//  
//  ItemService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.item;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.datatypes.Item;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.List;

import com.spacehopperstudios.utility.StringUtils;

final class ItemService implements IItemService {
	public String getName() {
		return ServiceType.ServiceTypeItem.toString();
	}

	@Override
	public Item getItem(Long id) {
		Item item = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection itemConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		String getItemQuery = String.format("SELECT * FROM `item` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
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
		item.creatorName = stripslashes(connection.getCurrentRowString("creatorname"));
		item.currency = stripslashes(connection.getCurrentRowString("currency"));
		item.externalId = stripslashes(connection.getCurrentRowString("externalid"));
		item.internalId = stripslashes(connection.getCurrentRowString("internalid"));
		item.name = stripslashes(connection.getCurrentRowString("name"));
		item.price = Float.valueOf((float) connection.getCurrentRowInteger("price").intValue() / 100.0f);
		item.properties = stripslashes(connection.getCurrentRowString("properties"));
		item.source = stripslashes(connection.getCurrentRowString("source"));
		item.type = stripslashes(connection.getCurrentRowString("type"));

		return item;
	}

	@Override
	public Item addItem(Item item) {
		Item addedItem = null;

		final String addItemQuery = String
				.format("INSERT INTO `item` (`externalid`,`internalid`,`name`,`creatorname`,`price`,`source`,`type`,`added`,`currency`,`properties`) VALUES ('%s','%s','%s','%s',%d,'%s','%s',FROM_UNIXTIME(%d),'%s','%s')",
						addslashes(item.externalId), addslashes(item.internalId), addslashes(item.name), addslashes(item.creatorName),
						(int) (item.price.floatValue() * 100.0f), addslashes(item.source), addslashes(item.type), item.added.getTime() / 1000,
						addslashes(item.currency), addslashes(item.properties));

		Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		try {
			itemConnection.connect();
			itemConnection.executeQuery(addItemQuery);

			if (itemConnection.getAffectedRowCount() > 0) {
				addedItem = getItem(Long.valueOf(itemConnection.getInsertedId()));
				
				if (addedItem == null) {
					addedItem = item;
					addedItem.id = Long.valueOf(itemConnection.getInsertedId());
				}
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
		Item updatedItem = null;

		Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());
		String updateItemQuery = String
				.format("UPDATE `item` SET `externalid`='%s',`internalid`='%s',`name`='%s',`creatorname`='%s',`price`=%d,`source`='%s',`type`='%s',`added`=FROM_UNIXTIME(%d),`currency`='%s',`properties`='%s' WHERE `id`=%d;",
						addslashes(item.externalId), addslashes(item.internalId), addslashes(item.name), addslashes(item.creatorName),
						(int) (item.price.floatValue() * 100.0f), addslashes(item.source), addslashes(item.type), item.added.getTime()/ 1000,
						addslashes(item.currency), addslashes(item.properties), item.id.longValue());

		try {
			itemConnection.connect();
			itemConnection.executeQuery(updateItemQuery);

			if (itemConnection.getAffectedRowCount() > 0) {
				updatedItem = getItem(item.id);
			} else {
				updatedItem = item;
			}

		} finally {
			if (itemConnection != null) {
				itemConnection.disconnect();
			}
		}

		return updatedItem;
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

		final String getExternalIdItemQuery = String.format("SELECT * FROM `item` WHERE `externalid` = '%s' and `deleted`='n'", addslashes(externalId));
		Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		try {
			itemConnection.connect();
			itemConnection.executeQuery(getExternalIdItemQuery);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.item.IItemService#getInternalIdItem(java.lang.String)
	 */
	@Override
	public Item getInternalIdItem(String internalId) {
		Item item = null;

		final String getInternalIdItemQuery = String.format("SELECT * FROM `item` WHERE `internalid`='%s' and `deleted`='n'", addslashes(internalId));

		Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

		try {
			itemConnection.connect();
			itemConnection.executeQuery(getInternalIdItemQuery);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.item.IItemService#getExternalIdItemBatch(java.util.List)
	 */
	@Override
	public List<Item> getExternalIdItemBatch(List<String> itemIds) {
		List<Item> items = new ArrayList<Item>();

		String commaDelimitedItemIds = null;

		if (itemIds != null && itemIds.size() > 0) {
			commaDelimitedItemIds = StringUtils.join(itemIds, "','");
		}

		if (commaDelimitedItemIds != null && commaDelimitedItemIds.length() != 0) {
			String getExternalIdItemBatchQuery = String.format("SELECT * FROM `item` WHERE `externalid` IN ('%s') and `deleted`='n'", commaDelimitedItemIds);

			Connection itemConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeItem.toString());

			try {
				itemConnection.connect();
				itemConnection.executeQuery(getExternalIdItemBatchQuery);

				while (itemConnection.fetchNextRow()) {
					Item item = toItem(itemConnection);

					if (item != null) {
						items.add(item);
					}
				}
			} finally {
				if (itemConnection != null) {
					itemConnection.disconnect();
				}
			}
		}

		return items;
	}

}