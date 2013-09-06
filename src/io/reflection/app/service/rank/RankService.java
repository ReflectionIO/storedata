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

		String getRankQuery = String.format("select * from `rank` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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
		rank.grossingPosition = connection.getCurrentRowInteger("grossingPosition");

		return rank;
	}

	@Override
	public Rank addRank(Rank rank) {
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

}