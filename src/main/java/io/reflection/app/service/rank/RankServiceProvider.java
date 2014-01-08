//  
//  RankServiceProvider.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class RankServiceProvider {

	/**
	 * @return
	 */
	public static IRankService provide() {
		IRankService rankService = null;

		if ((rankService = (IRankService) ServiceDiscovery.getService(ServiceType.ServiceTypeRank.toString())) == null) {
			rankService = RankServiceFactory.createNewRankService();
			ServiceDiscovery.registerService(rankService);
		}

		return rankService;
	}

}