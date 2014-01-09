//  
//  CountryServiceFactory.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.country;

final class CountryServiceFactory {

	/**
	 * @return
	 */
	public static ICountryService createNewCountryService() {
		return new CountryService();
	}

}