//  
//  InstallCountries.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.setup;

import static com.spacehopperstudios.storedata.objectify.PersistenceService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.spacehopperstudios.storedata.datatypes.Country;

/**
 * @author billy1380
 * 
 */
public class CountriesInstaller {
	public static void install() {
		ofy().save().entities(createCountry("Afghanistan", "AF", "AFG", 4), createCountry("Åland Islands", "AX", "ALA", 248),
				createCountry("Albania", "AL", "ALB", 8, Arrays.asList(new String[] { "ios" })),
				createCountry("Algeria", "DZ", "DZA", 12, Arrays.asList(new String[] { "ios" })), createCountry("American Samoa", "AS", "ASM", 16),
				createCountry("Andorra", "AD", "AND", 20), createCountry("Angola", "AO", "AGO", 24, Arrays.asList(new String[] { "ios" })),
				createCountry("Anguilla", "AI", "AIA", 660, Arrays.asList(new String[] { "ios" })), createCountry("Antarctica", "AQ", "ATA", 10),
				createCountry("Antigua and Barbuda", "AG", "ATG", 28, Arrays.asList(new String[] { "ios" })),
				createCountry("Argentina", "AR", "ARG", 32, Arrays.asList(new String[] { "ios" })),
				createCountry("Armenia", "AM", "ARM", 51, Arrays.asList(new String[] { "ios" })), createCountry("Aruba", "AW", "ABW", 533),
				createCountry("Australia", "AU", "AUS", 36, Arrays.asList(new String[] { "ios" })),
				createCountry("Austria", "AT", "AUT", 40, Arrays.asList(new String[] { "ios" })),
				createCountry("Azerbaijan", "AZ", "AZE", 31, Arrays.asList(new String[] { "ios" })),
				createCountry("Bahamas", "BS", "BHS", 44, Arrays.asList(new String[] { "ios" })),
				createCountry("Bahrain", "BH", "BHR", 48, Arrays.asList(new String[] { "ios" })), createCountry("Bangladesh", "BD", "BGD", 50),
				createCountry("Barbados", "BB", "BRB", 52, Arrays.asList(new String[] { "ios" })),
				createCountry("Belarus", "BY", "BLR", 112, Arrays.asList(new String[] { "ios" })),
				createCountry("Belgium", "BE", "BEL", 56, Arrays.asList(new String[] { "ios" })),
				createCountry("Belize", "BZ", "BLZ", 84, Arrays.asList(new String[] { "ios" })),
				createCountry("Benin", "BJ", "BEN", 204, Arrays.asList(new String[] { "ios" })),
				createCountry("Bermuda", "BM", "BMU", 60, Arrays.asList(new String[] { "ios" })),
				createCountry("Bhutan", "BT", "BTN", 64, Arrays.asList(new String[] { "ios" })),
				createCountry("Bolivia, Plurinational State of", "BO", "BOL", 68, Arrays.asList(new String[] { "ios" })),
				createCountry("Bonaire, Sint Eustatius and Saba", "BQ", "BES", 535), createCountry("Bosnia and Herzegovina", "BA", "BIH", 70),
				createCountry("Botswana", "BW", "BWA", 72, Arrays.asList(new String[] { "ios" })), createCountry("Bouvet Island", "BV", "BVT", 74),
				createCountry("Brazil", "BR", "BRA", 76, Arrays.asList(new String[] { "ios" })),
				createCountry("British Indian Ocean Territory", "IO", "IOT", 86),
				createCountry("Brunei Darussalam", "BN", "BRN", 96, Arrays.asList(new String[] { "ios" })),
				createCountry("Bulgaria", "BG", "BGR", 100, Arrays.asList(new String[] { "ios" })),
				createCountry("Burkina Faso", "BF", "BFA", 854, Arrays.asList(new String[] { "ios" })), createCountry("Burundi", "BI", "BDI", 108),
				createCountry("Cambodia", "KH", "KHM", 116, Arrays.asList(new String[] { "ios" })), createCountry("Cameroon", "CM", "CMR", 120),
				createCountry("Canada", "CA", "CAN", 124, Arrays.asList(new String[] { "ios" })),
				createCountry("Cape Verde", "CV", "CPV", 132, Arrays.asList(new String[] { "ios" })),
				createCountry("Cayman Islands", "KY", "CYM", 136, Arrays.asList(new String[] { "ios" })),
				createCountry("Central African Republic", "CF", "CAF", 140), createCountry("Chad", "TD", "TCD", 148, Arrays.asList(new String[] { "ios" })),
				createCountry("Chile", "CL", "CHL", 152, Arrays.asList(new String[] { "ios" })),
				createCountry("China", "CN", "CHN", 156, Arrays.asList(new String[] { "ios" })), createCountry("Christmas Island", "CX", "CXR", 162),
				createCountry("Cocos (Keeling) Islands", "CC", "CCK", 166), createCountry("Colombia", "CO", "COL", 170, Arrays.asList(new String[] { "ios" })),
				createCountry("Comoros", "KM", "COM", 174), createCountry("Congo", "CG", "COG", 178, Arrays.asList(new String[] { "ios" })),
				createCountry("Congo, the Democratic Republic of the", "CD", "COD", 180), createCountry("Cook Islands", "CK", "COK", 184),
				createCountry("Costa Rica", "CR", "CRI", 188, Arrays.asList(new String[] { "ios" })), createCountry("Côte d'Ivoire", "CI", "CIV", 384),
				createCountry("Croatia", "HR", "HRV", 191, Arrays.asList(new String[] { "ios" })), createCountry("Cuba", "CU", "CUB", 192),
				createCountry("Curaçao", "CW", "CUW", 531), createCountry("Cyprus", "CY", "CYP", 196, Arrays.asList(new String[] { "ios" })),
				createCountry("Czech Republic", "CZ", "CZE", 203, Arrays.asList(new String[] { "ios" })),
				createCountry("Denmark", "DK", "DNK", 208, Arrays.asList(new String[] { "ios" })), createCountry("Djibouti", "DJ", "DJI", 262),
				createCountry("Dominica", "DM", "DMA", 212, Arrays.asList(new String[] { "ios" })),
				createCountry("Dominican Republic", "DO", "DOM", 214, Arrays.asList(new String[] { "ios" })),
				createCountry("Ecuador", "EC", "ECU", 218, Arrays.asList(new String[] { "ios" })),
				createCountry("Egypt", "EG", "EGY", 818, Arrays.asList(new String[] { "ios" })),
				createCountry("El Salvador", "SV", "SLV", 222, Arrays.asList(new String[] { "ios" })), createCountry("Equatorial Guinea", "GQ", "GNQ", 226),
				createCountry("Eritrea", "ER", "ERI", 232), createCountry("Estonia", "EE", "EST", 233, Arrays.asList(new String[] { "ios" })),
				createCountry("Ethiopia", "ET", "ETH", 231), createCountry("Falkland Islands (Malvinas)", "FK", "FLK", 238),
				createCountry("Faroe Islands", "FO", "FRO", 234), createCountry("Fiji", "FJ", "FJI", 242, Arrays.asList(new String[] { "ios" })),
				createCountry("Finland", "FI", "FIN", 246, Arrays.asList(new String[] { "ios" })),
				createCountry("France", "FR", "FRA", 250, Arrays.asList(new String[] { "ios" })), createCountry("French Guiana", "GF", "GUF", 254),
				createCountry("French Polynesia", "PF", "PYF", 258), createCountry("French Southern Territories", "TF", "ATF", 260),
				createCountry("Gabon", "GA", "GAB", 266), createCountry("Gambia", "GM", "GMB", 270, Arrays.asList(new String[] { "ios" })),
				createCountry("Georgia", "GE", "GEO", 268), createCountry("Germany", "DE", "DEU", 276, Arrays.asList(new String[] { "ios" })),
				createCountry("Ghana", "GH", "GHA", 288, Arrays.asList(new String[] { "ios" })), createCountry("Gibraltar", "GI", "GIB", 292),
				createCountry("Greece", "GR", "GRC", 300, Arrays.asList(new String[] { "ios" })), createCountry("Greenland", "GL", "GRL", 304),
				createCountry("Grenada", "GD", "GRD", 308, Arrays.asList(new String[] { "ios" })), createCountry("Guadeloupe", "GP", "GLP", 312),
				createCountry("Guam", "GU", "GUM", 316), createCountry("Guatemala", "GT", "GTM", 320, Arrays.asList(new String[] { "ios" })),
				createCountry("Guernsey", "GG", "GGY", 831), createCountry("Guinea", "GN", "GIN", 324),
				createCountry("Guinea-Bissau", "GW", "GNB", 624, Arrays.asList(new String[] { "ios" })),
				createCountry("Guyana", "GY", "GUY", 328, Arrays.asList(new String[] { "ios" })), createCountry("Haiti", "HT", "HTI", 332),
				createCountry("Heard Island and McDonald Islands", "HM", "HMD", 334), createCountry("Holy See (Vatican City State)", "VA", "VAT", 336),
				createCountry("Honduras", "HN", "HND", 340, Arrays.asList(new String[] { "ios" })),
				createCountry("Hong Kong", "HK", "HKG", 344, Arrays.asList(new String[] { "ios" })),
				createCountry("Hungary", "HU", "HUN", 348, Arrays.asList(new String[] { "ios" })),
				createCountry("Iceland", "IS", "ISL", 352, Arrays.asList(new String[] { "ios" })),
				createCountry("India", "IN", "IND", 356, Arrays.asList(new String[] { "ios" })),
				createCountry("Indonesia", "ID", "IDN", 360, Arrays.asList(new String[] { "ios" })),
				createCountry("Iran, Islamic Republic of", "IR", "IRN", 364), createCountry("Iraq", "IQ", "IRQ", 368),
				createCountry("Ireland", "IE", "IRL", 372, Arrays.asList(new String[] { "ios" })), createCountry("Isle of Man", "IM", "IMN", 833),
				createCountry("Israel", "IL", "ISR", 376, Arrays.asList(new String[] { "ios" })),
				createCountry("Italy", "IT", "ITA", 380, Arrays.asList(new String[] { "ios" })),
				createCountry("Jamaica", "JM", "JAM", 388, Arrays.asList(new String[] { "ios" })),
				createCountry("Japan", "JP", "JPN", 392, Arrays.asList(new String[] { "ios" })), createCountry("Jersey", "JE", "JEY", 832),
				createCountry("Jordan", "JO", "JOR", 400, Arrays.asList(new String[] { "ios" })),
				createCountry("Kazakhstan", "KZ", "KAZ", 398, Arrays.asList(new String[] { "ios" })),
				createCountry("Kenya", "KE", "KEN", 404, Arrays.asList(new String[] { "ios" })), createCountry("Kiribati", "KI", "KIR", 296),
				createCountry("Korea, Democratic People's Republic of", "KP", "PRK", 408),
				createCountry("Korea, Republic of", "KR", "KOR", 410, Arrays.asList(new String[] { "ios" })),
				createCountry("Kuwait", "KW", "KWT", 414, Arrays.asList(new String[] { "ios" })),
				createCountry("Kyrgyzstan", "KG", "KGZ", 417, Arrays.asList(new String[] { "ios" })),
				createCountry("Lao People's Democratic Republic", "LA", "LAO", 418, Arrays.asList(new String[] { "ios" })),
				createCountry("Latvia", "LV", "LVA", 428, Arrays.asList(new String[] { "ios" })),
				createCountry("Lebanon", "LB", "LBN", 422, Arrays.asList(new String[] { "ios" })), createCountry("Lesotho", "LS", "LSO", 426),
				createCountry("Liberia", "LR", "LBR", 430, Arrays.asList(new String[] { "ios" })), createCountry("Libya", "LY", "LBY", 434),
				createCountry("Liechtenstein", "LI", "LIE", 438), createCountry("Lithuania", "LT", "LTU", 440, Arrays.asList(new String[] { "ios" })),
				createCountry("Luxembourg", "LU", "LUX", 442, Arrays.asList(new String[] { "ios" })),
				createCountry("Macao", "MO", "MAC", 446, Arrays.asList(new String[] { "ios" })),
				createCountry("Macedonia, The Former Yugoslav Republic of", "MK", "MKD", 807, Arrays.asList(new String[] { "ios" })),
				createCountry("Madagascar", "MG", "MDG", 450, Arrays.asList(new String[] { "ios" })),
				createCountry("Malawi", "MW", "MWI", 454, Arrays.asList(new String[] { "ios" })),
				createCountry("Malaysia", "MY", "MYS", 458, Arrays.asList(new String[] { "ios" })), createCountry("Maldives", "MV", "MDV", 462),
				createCountry("Mali", "ML", "MLI", 466, Arrays.asList(new String[] { "ios" })),
				createCountry("Malta", "MT", "MLT", 470, Arrays.asList(new String[] { "ios" })), createCountry("Marshall Islands", "MH", "MHL", 584),
				createCountry("Martinique", "MQ", "MTQ", 474), createCountry("Mauritania", "MR", "MRT", 478, Arrays.asList(new String[] { "ios" })),
				createCountry("Mauritius", "MU", "MUS", 480, Arrays.asList(new String[] { "ios" })), createCountry("Mayotte", "YT", "MYT", 175),
				createCountry("Mexico", "MX", "MEX", 484, Arrays.asList(new String[] { "ios" })),
				createCountry("Micronesia, Federated States of", "FM", "FSM", 583, Arrays.asList(new String[] { "ios" })),
				createCountry("Moldova, Republic of", "MD", "MDA", 498, Arrays.asList(new String[] { "ios" })), createCountry("Monaco", "MC", "MCO", 492),
				createCountry("Mongolia", "MN", "MNG", 496, Arrays.asList(new String[] { "ios" })), createCountry("Montenegro", "ME", "MNE", 499),
				createCountry("Montserrat", "MS", "MSR", 500, Arrays.asList(new String[] { "ios" })), createCountry("Morocco", "MA", "MAR", 504),
				createCountry("Mozambique", "MZ", "MOZ", 508, Arrays.asList(new String[] { "ios" })), createCountry("Myanmar", "MM", "MMR", 104),
				createCountry("Namibia", "NA", "NAM", 516, Arrays.asList(new String[] { "ios" })), createCountry("Nauru", "NR", "NRU", 520),
				createCountry("Nepal", "NP", "NPL", 524, Arrays.asList(new String[] { "ios" })),
				createCountry("Netherlands", "NL", "NLD", 528, Arrays.asList(new String[] { "ios" })), createCountry("New Caledonia", "NC", "NCL", 540),
				createCountry("New Zealand", "NZ", "NZL", 554, Arrays.asList(new String[] { "ios" })),
				createCountry("Nicaragua", "NI", "NIC", 558, Arrays.asList(new String[] { "ios" })),
				createCountry("Niger", "NE", "NER", 562, Arrays.asList(new String[] { "ios" })),
				createCountry("Nigeria", "NG", "NGA", 566, Arrays.asList(new String[] { "ios" })), createCountry("Niue", "NU", "NIU", 570),
				createCountry("Norfolk Island", "NF", "NFK", 574), createCountry("Northern Mariana Islands", "MP", "MNP", 580),
				createCountry("Norway", "NO", "NOR", 578, Arrays.asList(new String[] { "ios" })),
				createCountry("Oman", "OM", "OMN", 512, Arrays.asList(new String[] { "ios" })),
				createCountry("Pakistan", "PK", "PAK", 586, Arrays.asList(new String[] { "ios" })),
				createCountry("Palau", "PW", "PLW", 585, Arrays.asList(new String[] { "ios" })), createCountry("Palestine, State of", "PS", "PSE", 275),
				createCountry("Panama", "PA", "PAN", 591, Arrays.asList(new String[] { "ios" })),
				createCountry("Papua New Guinea", "PG", "PNG", 598, Arrays.asList(new String[] { "ios" })),
				createCountry("Paraguay", "PY", "PRY", 600, Arrays.asList(new String[] { "ios" })),
				createCountry("Peru", "PE", "PER", 604, Arrays.asList(new String[] { "ios" })),
				createCountry("Philippines", "PH", "PHL", 608, Arrays.asList(new String[] { "ios" })), createCountry("Pitcairn", "PN", "PCN", 612),
				createCountry("Poland", "PL", "POL", 616, Arrays.asList(new String[] { "ios" })),
				createCountry("Portugal", "PT", "PRT", 620, Arrays.asList(new String[] { "ios" })), createCountry("Puerto Rico", "PR", "PRI", 630),
				createCountry("Qatar", "QA", "QAT", 634, Arrays.asList(new String[] { "ios" })), createCountry("Réunion", "RE", "REU", 638),
				createCountry("Romania", "RO", "ROU", 642, Arrays.asList(new String[] { "ios" })),
				createCountry("Russian Federation", "RU", "RUS", 643, Arrays.asList(new String[] { "ios" })), createCountry("Rwanda", "RW", "RWA", 646),
				createCountry("Saint Barthélemy", "BL", "BLM", 652), createCountry("Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", 654),
				createCountry("Saint Kitts and Nevis", "KN", "KNA", 659, Arrays.asList(new String[] { "ios" })),
				createCountry("Saint Lucia", "LC", "LCA", 662, Arrays.asList(new String[] { "ios" })),
				createCountry("Saint Martin (French part)", "MF", "MAF", 663), createCountry("Saint Pierre and Miquelon", "PM", "SPM", 666),
				createCountry("Saint Vincent and the Grenadines", "VC", "VCT", 670, Arrays.asList(new String[] { "ios" })),
				createCountry("Samoa", "WS", "WSM", 882), createCountry("San Marino", "SM", "SMR", 674),
				createCountry("Sao Tome and Principe", "ST", "STP", 678, Arrays.asList(new String[] { "ios" })),
				createCountry("Saudi Arabia", "SA", "SAU", 682, Arrays.asList(new String[] { "ios" })),
				createCountry("Senegal", "SN", "SEN", 686, Arrays.asList(new String[] { "ios" })), createCountry("Serbia", "RS", "SRB", 688),
				createCountry("Seychelles", "SC", "SYC", 690, Arrays.asList(new String[] { "ios" })),
				createCountry("Sierra Leone", "SL", "SLE", 694, Arrays.asList(new String[] { "ios" })),
				createCountry("Singapore", "SG", "SGP", 702, Arrays.asList(new String[] { "ios" })),
				createCountry("Sint Maarten (Dutch part)", "SX", "SXM", 534),
				createCountry("Slovakia", "SK", "SVK", 703, Arrays.asList(new String[] { "ios" })),
				createCountry("Slovenia", "SI", "SVN", 705, Arrays.asList(new String[] { "ios" })),
				createCountry("Solomon Islands", "SB", "SLB", 90, Arrays.asList(new String[] { "ios" })), createCountry("Somalia", "SO", "SOM", 706),
				createCountry("South Africa", "ZA", "ZAF", 710, Arrays.asList(new String[] { "ios" })),
				createCountry("South Georgia and the South Sandwich Islands", "GS", "SGS", 239), createCountry("South Sudan", "SS", "SSD", 728),
				createCountry("Spain", "ES", "ESP", 724, Arrays.asList(new String[] { "ios" })),
				createCountry("Sri Lanka", "LK", "LKA", 144, Arrays.asList(new String[] { "ios" })), createCountry("Sudan", "SD", "SDN", 729),
				createCountry("Suriname", "SR", "SUR", 740, Arrays.asList(new String[] { "ios" })), createCountry("Svalbard and Jan Mayen", "SJ", "SJM", 744),
				createCountry("Swaziland", "SZ", "SWZ", 748, Arrays.asList(new String[] { "ios" })),
				createCountry("Sweden", "SE", "SWE", 752, Arrays.asList(new String[] { "ios" })),
				createCountry("Switzerland", "CH", "CHE", 756, Arrays.asList(new String[] { "ios" })), createCountry("Syrian Arab Republic", "SY", "SYR", 760),
				createCountry("Taiwan, Province of China", "TW", "TWN", 158, Arrays.asList(new String[] { "ios" })),
				createCountry("Tajikistan", "TJ", "TJK", 762, Arrays.asList(new String[] { "ios" })),
				createCountry("Tanzania, United Republic of", "TZ", "TZA", 834, Arrays.asList(new String[] { "ios" })),
				createCountry("Thailand", "TH", "THA", 764, Arrays.asList(new String[] { "ios" })), createCountry("Timor-Leste", "TL", "TLS", 626),
				createCountry("Togo", "TG", "TGO", 768), createCountry("Tokelau", "TK", "TKL", 772), createCountry("Tonga", "TO", "TON", 776),
				createCountry("Trinidad and Tobago", "TT", "TTO", 780, Arrays.asList(new String[] { "ios" })),
				createCountry("Tunisia", "TN", "TUN", 788, Arrays.asList(new String[] { "ios" })),
				createCountry("Turkey", "TR", "TUR", 792, Arrays.asList(new String[] { "ios" })),
				createCountry("Turkmenistan", "TM", "TKM", 795, Arrays.asList(new String[] { "ios" })),
				createCountry("Turks and Caicos Islands", "TC", "TCA", 796, Arrays.asList(new String[] { "ios" })), createCountry("Tuvalu", "TV", "TUV", 798),
				createCountry("Uganda", "UG", "UGA", 800, Arrays.asList(new String[] { "ios" })),
				createCountry("Ukraine", "UA", "UKR", 804, Arrays.asList(new String[] { "ios" })),
				createCountry("United Arab Emirates", "AE", "ARE", 784, Arrays.asList(new String[] { "ios" })),
				createCountry("United Kingdom", "GB", "GBR", 826, Arrays.asList(new String[] { "ios" })),
				createCountry("United States", "US", "USA", 840, Arrays.asList(new String[] { "ios" })),
				createCountry("United States Minor Outlying Islands", "UM", "UMI", 581),
				createCountry("Uruguay", "UY", "URY", 858, Arrays.asList(new String[] { "ios" })),
				createCountry("Uzbekistan", "UZ", "UZB", 860, Arrays.asList(new String[] { "ios" })), createCountry("Vanuatu", "VU", "VUT", 548),
				createCountry("Venezuela, Bolivarian Republic of", "VE", "VEN", 862, Arrays.asList(new String[] { "ios" })),
				createCountry("Viet Nam", "VN", "VNM", 704, Arrays.asList(new String[] { "ios" })),
				createCountry("Virgin Islands, British", "VG", "VGB", 92, Arrays.asList(new String[] { "ios" })),
				createCountry("Virgin Islands, U.S.", "VI", "VIR", 850), createCountry("Wallis and Futuna", "WF", "WLF", 876),
				createCountry("Western Sahara", "EH", "ESH", 732), createCountry("Yemen", "YE", "YEM", 887, Arrays.asList(new String[] { "ios" })),
				createCountry("Zambia", "ZM", "ZMB", 894), createCountry("Zimbabwe", "ZW", "ZWE", 716, Arrays.asList(new String[] { "ios" })));
	}

	private static Country createCountry(String shortName, String alpha2Code, String alpha3Code, int numericCode) {
		return createCountry(shortName, alpha2Code, alpha3Code, numericCode, null);
	}

	private static Country createCountry(String shortName, String alpha2Code, String alpha3Code, int numericCode, List<String> stores) {
		Country c = new Country();
		c.name = shortName;
		c.a2Code = alpha2Code;
		c.a3Code = alpha3Code;
		c.nCode = Integer.valueOf(numericCode);

		if (stores == null) {
			c.stores = new ArrayList<String>();
		} else {
			c.stores = stores;
		}

		return c;
	}

}
