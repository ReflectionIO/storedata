//  
//  RankService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import io.reflection.app.datatypes.Rank;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class RankService implements IRankService {
	public String getName() {
		return ServiceType.ServiceTypeRank.toString();
	}

	@Override
	public Rank getRank(Long id) {
		Rank rank = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection rankConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		String getRankQuery = String.format("SELECT * FROM `rank` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRankQuery);

			if (rankConnection.fetchNextRow()) {
				rank = toRank(rankConnection);
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}
		return rank;
	}

	/**
	 * To rank
	 * 
	 * @param connection
	 * @return
	 */
	private Rank toRank(Connection connection) {
		Rank rank = new Rank();

		rank.id = connection.getCurrentRowLong("id");
		rank.created = connection.getCurrentRowDateTime("created");
		rank.deleted = connection.getCurrentRowString("deleted");

		rank.code = connection.getCurrentRowString("code");
		rank.country = connection.getCurrentRowString("country");
		rank.currency = connection.getCurrentRowString("currency");
		rank.date = connection.getCurrentRowDateTime("date");
		rank.grossingPosition = connection.getCurrentRowInteger("grossingposition");
		rank.itemId = connection.getCurrentRowString("itemid");
		rank.position = connection.getCurrentRowInteger("possition");
		rank.price = Float.valueOf((float) connection.getCurrentRowInteger("price").intValue() / 100.0f);
		rank.source = connection.getCurrentRowString("source");
		rank.type = connection.getCurrentRowString("type");

		return rank;
	}

	@Override
	public Rank addRank(Rank rank) {
		// insert into rank (created,position,grossingposition,itemid,type,country,date,source,price,currency,code) values (?,?,?,?,?,?,?,?,?,?,?);
		throw new UnsupportedOperationException();
	}

	@Override
	public Rank updateRank(Rank rank) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteRank(Rank rank) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getItemGatherCodeRank(java.lang.String, java.lang.String)
	 */
	@Override
	public Rank getItemGatherCodeRank(String itemId, String code) {
		Rank rank = null;
		
		String query = String.format("SELECT * FROM `rank` WHERE `itemid`='%s' AND `code`='%s' AND `deleted`='n' LIMIT 1", itemId, code);
		
		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());
		
		try {
			rankConnection.connect();
			rankConnection.executeQuery(query);
			
			if (rankConnection.fetchNextRow()) {
				rank = toRank(rankConnection);
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}
		
		return rank;
	}
}