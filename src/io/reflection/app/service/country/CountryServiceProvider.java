//  
//  CountryServiceProvider.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.country;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class CountryServiceProvider {

	/**
	 * @return
	 */
	public static ICountryService provide() {
		ICountryService countryService = null;

		if ((countryService = (ICountryService) ServiceDiscovery.getService(ServiceType.ServiceTypeCountry.toString())) == null) {
			countryService = CountryServiceFactory.createNewCountryService();
			ServiceDiscovery.registerService(countryService);
		}

		return countryService;
	}

}