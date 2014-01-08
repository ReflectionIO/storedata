//  
//  InstallStores.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.setup;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.datatypes.Store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author billy1380
 * 
 */
public class StoresInstaller {

	public static void install() throws DataAccessException {

		if (StoreServiceProvider.provide().getStoresCount() == 0) {
			// LATER add stores to database
			
			ofy().save().entities(
					createStore(
							"Apple iOS Store",
							"ios",
							"http://www.apple.com",
							Arrays.asList(new String[] { "al", "dz", "ao", "ai", "ag", "ar", "am", "au", "at", "az", "bs", "bh", "bb", "by", "be", "bz", "bj",
									"bm", "bt", "bo", "bw", "br", "bn", "bg", "bf", "kh", "ca", "cv", "ky", "td", "cl", "cn", "co", "cg", "cr", "hr", "cy",
									"cz", "dk", "dm", "do", "ec", "eg", "sv", "ee", "fj", "fi", "fr", "gm", "de", "gh", "gr", "gd", "gt", "gw", "gy", "hn",
									"hk", "hu", "is", "in", "id", "ie", "il", "it", "jm", "jp", "jo", "kz", "ke", "kr", "kw", "kg", "la", "lv", "lb", "lr",
									"lt", "lu", "mo", "mk", "mg", "mw", "my", "ml", "mt", "mr", "mu", "mx", "fm", "md", "mn", "ms", "mz", "na", "np", "nl",
									"nz", "ni", "ne", "ng", "no", "om", "pk", "pw", "pa", "pg", "py", "pe", "ph", "pl", "pt", "qa", "ro", "ru", "st", "sa",
									"sn", "sc", "sl", "sg", "sk", "si", "sb", "za", "es", "lk", "kn", "lc", "vc", "sr", "sz", "se", "ch", "tw", "tj", "tz",
									"th", "tt", "tn", "tr", "tm", "tc", "ug", "gb", "ua", "ae", "uy", "us", "uz", "ve", "vn", "vg", "ye", "zw" })),
					createStore("Apple Mac OS X Store", "mac", "http://www.apple.com"), createStore("Android Play Store", "gpl", "http://play.google.com"),
					createStore("Amazon Appstore for Android", "azn", "http://www.amazon.com"));
		}
	}

	private static Store createStore(String name, String a3Code, String url) {
		return createStore(name, a3Code, url, null);
	}

	private static Store createStore(String name, String a3Code, String url, List<String> countries) {
		Store s = new Store();

		s.a3Code = a3Code;
		s.name = name;
		s.url = url;

		if (countries == null) {
			s.countries = new ArrayList<String>();
		} else {
			s.countries = countries;
		}

		return s;
	}

}
