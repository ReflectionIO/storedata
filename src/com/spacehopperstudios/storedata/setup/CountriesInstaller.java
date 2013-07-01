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
		ofy().save().entities(createCountry("Afghanistan", "af", "afg", 4), createCountry("Åland Islands", "ax", "ala", 248),
				createCountry("Albania", "al", "alb", 8, Arrays.asList(new String[] { "ios" })),
				createCountry("Algeria", "dz", "dza", 12, Arrays.asList(new String[] { "ios" })), createCountry("American Samoa", "as", "asm", 16),
				createCountry("Andorra", "ad", "and", 20), createCountry("Angola", "ao", "ago", 24, Arrays.asList(new String[] { "ios" })),
				createCountry("Anguilla", "ai", "aia", 660, Arrays.asList(new String[] { "ios" })), createCountry("Antarctica", "aq", "ata", 10),
				createCountry("Antigua and Barbuda", "ag", "atg", 28, Arrays.asList(new String[] { "ios" })),
				createCountry("Argentina", "ar", "arg", 32, Arrays.asList(new String[] { "ios" })),
				createCountry("Armenia", "am", "arm", 51, Arrays.asList(new String[] { "ios" })), createCountry("Aruba", "aw", "abw", 533),
				createCountry("Australia", "au", "aus", 36, Arrays.asList(new String[] { "ios" })),
				createCountry("Austria", "at", "aut", 40, Arrays.asList(new String[] { "ios" })),
				createCountry("Azerbaijan", "az", "aze", 31, Arrays.asList(new String[] { "ios" })),
				createCountry("Bahamas", "bs", "bhs", 44, Arrays.asList(new String[] { "ios" })),
				createCountry("Bahrain", "bh", "bhr", 48, Arrays.asList(new String[] { "ios" })), createCountry("Bangladesh", "bd", "bgd", 50),
				createCountry("Barbados", "bb", "brb", 52, Arrays.asList(new String[] { "ios" })),
				createCountry("Belarus", "by", "blr", 112, Arrays.asList(new String[] { "ios" })),
				createCountry("Belgium", "be", "bel", 56, Arrays.asList(new String[] { "ios" })),
				createCountry("Belize", "bz", "blz", 84, Arrays.asList(new String[] { "ios" })),
				createCountry("Benin", "bj", "ben", 204, Arrays.asList(new String[] { "ios" })),
				createCountry("Bermuda", "bm", "bmu", 60, Arrays.asList(new String[] { "ios" })),
				createCountry("Bhutan", "bt", "btn", 64, Arrays.asList(new String[] { "ios" })),
				createCountry("Bolivia, Plurinational State of", "bo", "bol", 68, Arrays.asList(new String[] { "ios" })),
				createCountry("Bonaire, Sint Eustatius and Saba", "bq", "bes", 535), createCountry("Bosnia and Herzegovina", "ba", "bih", 70),
				createCountry("Botswana", "bw", "bwa", 72, Arrays.asList(new String[] { "ios" })), createCountry("Bouvet Island", "bv", "bvt", 74),
				createCountry("Brazil", "br", "bra", 76, Arrays.asList(new String[] { "ios" })),
				createCountry("British Indian Ocean Territory", "io", "iot", 86),
				createCountry("Brunei Darussalam", "bn", "brn", 96, Arrays.asList(new String[] { "ios" })),
				createCountry("Bulgaria", "bg", "bgr", 100, Arrays.asList(new String[] { "ios" })),
				createCountry("Burkina Faso", "bf", "bfa", 854, Arrays.asList(new String[] { "ios" })), createCountry("Burundi", "bi", "bdi", 108),
				createCountry("Cambodia", "kh", "khm", 116, Arrays.asList(new String[] { "ios" })), createCountry("Cameroon", "cm", "cmr", 120),
				createCountry("Canada", "ca", "can", 124, Arrays.asList(new String[] { "ios" })),
				createCountry("Cape Verde", "cv", "cpv", 132, Arrays.asList(new String[] { "ios" })),
				createCountry("Cayman Islands", "ky", "cym", 136, Arrays.asList(new String[] { "ios" })),
				createCountry("Central African Republic", "cf", "caf", 140), createCountry("Chad", "td", "tcd", 148, Arrays.asList(new String[] { "ios" })),
				createCountry("Chile", "cl", "chl", 152, Arrays.asList(new String[] { "ios" })),
				createCountry("China", "cn", "chn", 156, Arrays.asList(new String[] { "ios" })), createCountry("Christmas Island", "cx", "cxr", 162),
				createCountry("Cocos (Keeling) Islands", "cc", "cck", 166), createCountry("Colombia", "co", "col", 170, Arrays.asList(new String[] { "ios" })),
				createCountry("Comoros", "km", "com", 174), createCountry("Congo", "cg", "cog", 178, Arrays.asList(new String[] { "ios" })),
				createCountry("Congo, the Democratic Republic of the", "cd", "cod", 180), createCountry("Cook Islands", "ck", "cok", 184),
				createCountry("Costa Rica", "cr", "cri", 188, Arrays.asList(new String[] { "ios" })), createCountry("Côte d'Ivoire", "ci", "civ", 384),
				createCountry("Croatia", "hr", "hrv", 191, Arrays.asList(new String[] { "ios" })), createCountry("Cuba", "cu", "cub", 192),
				createCountry("Curaçao", "cw", "cuw", 531), createCountry("Cyprus", "cy", "cyp", 196, Arrays.asList(new String[] { "ios" })),
				createCountry("Czech Republic", "cz", "cze", 203, Arrays.asList(new String[] { "ios" })),
				createCountry("Denmark", "dk", "dnk", 208, Arrays.asList(new String[] { "ios" })), createCountry("Djibouti", "dj", "dji", 262),
				createCountry("Dominica", "dm", "dma", 212, Arrays.asList(new String[] { "ios" })),
				createCountry("Dominican Republic", "do", "dom", 214, Arrays.asList(new String[] { "ios" })),
				createCountry("Ecuador", "ec", "ecu", 218, Arrays.asList(new String[] { "ios" })),
				createCountry("Egypt", "eg", "egy", 818, Arrays.asList(new String[] { "ios" })),
				createCountry("El Salvador", "sv", "slv", 222, Arrays.asList(new String[] { "ios" })), createCountry("Equatorial Guinea", "gq", "gnq", 226),
				createCountry("Eritrea", "er", "eri", 232), createCountry("Estonia", "ee", "est", 233, Arrays.asList(new String[] { "ios" })),
				createCountry("Ethiopia", "et", "eth", 231), createCountry("Falkland Islands (Malvinas)", "fk", "flk", 238),
				createCountry("Faroe Islands", "fo", "fro", 234), createCountry("Fiji", "fj", "fji", 242, Arrays.asList(new String[] { "ios" })),
				createCountry("Finland", "fi", "fin", 246, Arrays.asList(new String[] { "ios" })),
				createCountry("France", "fr", "fra", 250, Arrays.asList(new String[] { "ios" })), createCountry("French Guiana", "gf", "guf", 254),
				createCountry("French Polynesia", "pf", "pyf", 258), createCountry("French Southern Territories", "tf", "atf", 260),
				createCountry("Gabon", "ga", "gab", 266), createCountry("Gambia", "gm", "gmb", 270, Arrays.asList(new String[] { "ios" })),
				createCountry("Georgia", "ge", "geo", 268), createCountry("Germany", "de", "deu", 276, Arrays.asList(new String[] { "ios" })),
				createCountry("Ghana", "gh", "gha", 288, Arrays.asList(new String[] { "ios" })), createCountry("Gibraltar", "gi", "gib", 292),
				createCountry("Greece", "gr", "grc", 300, Arrays.asList(new String[] { "ios" })), createCountry("Greenland", "gl", "grl", 304),
				createCountry("Grenada", "gd", "grd", 308, Arrays.asList(new String[] { "ios" })), createCountry("Guadeloupe", "gp", "glp", 312),
				createCountry("Guam", "gu", "gum", 316), createCountry("Guatemala", "gt", "gtm", 320, Arrays.asList(new String[] { "ios" })),
				createCountry("Guernsey", "gg", "ggy", 831), createCountry("Guinea", "gn", "gin", 324),
				createCountry("Guinea-Bissau", "gw", "gnb", 624, Arrays.asList(new String[] { "ios" })),
				createCountry("Guyana", "gy", "guy", 328, Arrays.asList(new String[] { "ios" })), createCountry("Haiti", "ht", "hti", 332),
				createCountry("Heard Island and McDonald Islands", "hm", "hmd", 334), createCountry("Holy See (Vatican City State)", "va", "vat", 336),
				createCountry("Honduras", "hn", "hnd", 340, Arrays.asList(new String[] { "ios" })),
				createCountry("Hong Kong", "hk", "hkg", 344, Arrays.asList(new String[] { "ios" })),
				createCountry("Hungary", "hu", "hun", 348, Arrays.asList(new String[] { "ios" })),
				createCountry("Iceland", "is", "isl", 352, Arrays.asList(new String[] { "ios" })),
				createCountry("India", "in", "ind", 356, Arrays.asList(new String[] { "ios" })),
				createCountry("Indonesia", "id", "idn", 360, Arrays.asList(new String[] { "ios" })),
				createCountry("Iran, Islamic Republic of", "ir", "irn", 364), createCountry("Iraq", "iq", "irq", 368),
				createCountry("Ireland", "ie", "irl", 372, Arrays.asList(new String[] { "ios" })), createCountry("Isle of Man", "im", "imn", 833),
				createCountry("Israel", "il", "isr", 376, Arrays.asList(new String[] { "ios" })),
				createCountry("Italy", "it", "ita", 380, Arrays.asList(new String[] { "ios" })),
				createCountry("Jamaica", "jm", "jam", 388, Arrays.asList(new String[] { "ios" })),
				createCountry("Japan", "jp", "jpn", 392, Arrays.asList(new String[] { "ios" })), createCountry("Jersey", "je", "jey", 832),
				createCountry("Jordan", "jo", "jor", 400, Arrays.asList(new String[] { "ios" })),
				createCountry("Kazakhstan", "kz", "kaz", 398, Arrays.asList(new String[] { "ios" })),
				createCountry("Kenya", "ke", "ken", 404, Arrays.asList(new String[] { "ios" })), createCountry("Kiribati", "ki", "kir", 296),
				createCountry("Korea, Democratic People's Republic of", "kp", "prk", 408),
				createCountry("Korea, Republic of", "kr", "kor", 410, Arrays.asList(new String[] { "ios" })),
				createCountry("Kuwait", "kw", "kwt", 414, Arrays.asList(new String[] { "ios" })),
				createCountry("Kyrgyzstan", "kg", "kgz", 417, Arrays.asList(new String[] { "ios" })),
				createCountry("Lao People's Democratic Republic", "la", "lao", 418, Arrays.asList(new String[] { "ios" })),
				createCountry("Latvia", "lv", "lva", 428, Arrays.asList(new String[] { "ios" })),
				createCountry("Lebanon", "lb", "lbn", 422, Arrays.asList(new String[] { "ios" })), createCountry("Lesotho", "ls", "lso", 426),
				createCountry("Liberia", "lr", "lbr", 430, Arrays.asList(new String[] { "ios" })), createCountry("Libya", "ly", "lby", 434),
				createCountry("Liechtenstein", "li", "lie", 438), createCountry("Lithuania", "lt", "ltu", 440, Arrays.asList(new String[] { "ios" })),
				createCountry("Luxembourg", "lu", "lux", 442, Arrays.asList(new String[] { "ios" })),
				createCountry("Macao", "mo", "mac", 446, Arrays.asList(new String[] { "ios" })),
				createCountry("Macedonia, The Former Yugoslav Republic of", "mk", "mkd", 807, Arrays.asList(new String[] { "ios" })),
				createCountry("Madagascar", "mg", "mdg", 450, Arrays.asList(new String[] { "ios" })),
				createCountry("Malawi", "mw", "mwi", 454, Arrays.asList(new String[] { "ios" })),
				createCountry("Malaysia", "my", "mys", 458, Arrays.asList(new String[] { "ios" })), createCountry("Maldives", "mv", "mdv", 462),
				createCountry("Mali", "ml", "mli", 466, Arrays.asList(new String[] { "ios" })),
				createCountry("Malta", "mt", "mlt", 470, Arrays.asList(new String[] { "ios" })), createCountry("Marshall Islands", "mh", "mhl", 584),
				createCountry("Martinique", "mq", "mtq", 474), createCountry("Mauritania", "mr", "mrt", 478, Arrays.asList(new String[] { "ios" })),
				createCountry("Mauritius", "mu", "mus", 480, Arrays.asList(new String[] { "ios" })), createCountry("Mayotte", "yt", "myt", 175),
				createCountry("Mexico", "mx", "mex", 484, Arrays.asList(new String[] { "ios" })),
				createCountry("Micronesia, Federated States of", "fm", "fsm", 583, Arrays.asList(new String[] { "ios" })),
				createCountry("Moldova, Republic of", "md", "mda", 498, Arrays.asList(new String[] { "ios" })), createCountry("Monaco", "mc", "mco", 492),
				createCountry("Mongolia", "mn", "mng", 496, Arrays.asList(new String[] { "ios" })), createCountry("Montenegro", "me", "mne", 499),
				createCountry("Montserrat", "ms", "msr", 500, Arrays.asList(new String[] { "ios" })), createCountry("Morocco", "ma", "mar", 504),
				createCountry("Mozambique", "mz", "moz", 508, Arrays.asList(new String[] { "ios" })), createCountry("Myanmar", "mm", "mmr", 104),
				createCountry("Namibia", "na", "nam", 516, Arrays.asList(new String[] { "ios" })), createCountry("Nauru", "nr", "nru", 520),
				createCountry("Nepal", "np", "npl", 524, Arrays.asList(new String[] { "ios" })),
				createCountry("Netherlands", "nl", "nld", 528, Arrays.asList(new String[] { "ios" })), createCountry("New Caledonia", "nc", "ncl", 540),
				createCountry("New Zealand", "nz", "nzl", 554, Arrays.asList(new String[] { "ios" })),
				createCountry("Nicaragua", "ni", "nic", 558, Arrays.asList(new String[] { "ios" })),
				createCountry("Niger", "ne", "ner", 562, Arrays.asList(new String[] { "ios" })),
				createCountry("Nigeria", "ng", "nga", 566, Arrays.asList(new String[] { "ios" })), createCountry("Niue", "nu", "niu", 570),
				createCountry("Norfolk Island", "nf", "nfk", 574), createCountry("Northern Mariana Islands", "mp", "mnp", 580),
				createCountry("Norway", "no", "nor", 578, Arrays.asList(new String[] { "ios" })),
				createCountry("Oman", "om", "omn", 512, Arrays.asList(new String[] { "ios" })),
				createCountry("Pakistan", "pk", "pak", 586, Arrays.asList(new String[] { "ios" })),
				createCountry("Palau", "pw", "plw", 585, Arrays.asList(new String[] { "ios" })), createCountry("Palestine, State of", "ps", "pse", 275),
				createCountry("Panama", "pa", "pan", 591, Arrays.asList(new String[] { "ios" })),
				createCountry("Papua New Guinea", "pg", "png", 598, Arrays.asList(new String[] { "ios" })),
				createCountry("Paraguay", "py", "pry", 600, Arrays.asList(new String[] { "ios" })),
				createCountry("Peru", "pe", "per", 604, Arrays.asList(new String[] { "ios" })),
				createCountry("Philippines", "ph", "phl", 608, Arrays.asList(new String[] { "ios" })), createCountry("Pitcairn", "pn", "pcn", 612),
				createCountry("Poland", "pl", "pol", 616, Arrays.asList(new String[] { "ios" })),
				createCountry("Portugal", "pt", "prt", 620, Arrays.asList(new String[] { "ios" })), createCountry("Puerto Rico", "pr", "pri", 630),
				createCountry("Qatar", "qa", "qat", 634, Arrays.asList(new String[] { "ios" })), createCountry("Réunion", "re", "reu", 638),
				createCountry("Romania", "ro", "rou", 642, Arrays.asList(new String[] { "ios" })),
				createCountry("Russian Federation", "ru", "rus", 643, Arrays.asList(new String[] { "ios" })), createCountry("Rwanda", "rw", "rwa", 646),
				createCountry("Saint Barthélemy", "bl", "blm", 652), createCountry("Saint Helena, Ascension and Tristan da Cunha", "sh", "shn", 654),
				createCountry("Saint Kitts and Nevis", "kn", "kna", 659, Arrays.asList(new String[] { "ios" })),
				createCountry("Saint Lucia", "lc", "lca", 662, Arrays.asList(new String[] { "ios" })),
				createCountry("Saint Martin (French part)", "mf", "maf", 663), createCountry("Saint Pierre and Miquelon", "pm", "spm", 666),
				createCountry("Saint Vincent and the Grenadines", "vc", "vct", 670, Arrays.asList(new String[] { "ios" })),
				createCountry("Samoa", "ws", "wsm", 882), createCountry("San Marino", "sm", "smr", 674),
				createCountry("Sao Tome and Principe", "st", "stp", 678, Arrays.asList(new String[] { "ios" })),
				createCountry("Saudi Arabia", "sa", "sau", 682, Arrays.asList(new String[] { "ios" })),
				createCountry("Senegal", "sn", "sen", 686, Arrays.asList(new String[] { "ios" })), createCountry("Serbia", "rs", "srb", 688),
				createCountry("Seychelles", "sc", "syc", 690, Arrays.asList(new String[] { "ios" })),
				createCountry("Sierra Leone", "sl", "sle", 694, Arrays.asList(new String[] { "ios" })),
				createCountry("Singapore", "sg", "sgp", 702, Arrays.asList(new String[] { "ios" })),
				createCountry("Sint Maarten (Dutch part)", "sx", "sxm", 534),
				createCountry("Slovakia", "sk", "svk", 703, Arrays.asList(new String[] { "ios" })),
				createCountry("Slovenia", "si", "svn", 705, Arrays.asList(new String[] { "ios" })),
				createCountry("Solomon Islands", "sb", "slb", 90, Arrays.asList(new String[] { "ios" })), createCountry("Somalia", "so", "som", 706),
				createCountry("South Africa", "za", "zaf", 710, Arrays.asList(new String[] { "ios" })),
				createCountry("South Georgia and the South Sandwich Islands", "gs", "sgs", 239), createCountry("South Sudan", "ss", "ssd", 728),
				createCountry("Spain", "es", "esp", 724, Arrays.asList(new String[] { "ios" })),
				createCountry("Sri Lanka", "lk", "lka", 144, Arrays.asList(new String[] { "ios" })), createCountry("Sudan", "sd", "sdn", 729),
				createCountry("Suriname", "sr", "sur", 740, Arrays.asList(new String[] { "ios" })), createCountry("Svalbard and Jan Mayen", "sj", "sjm", 744),
				createCountry("Swaziland", "sz", "swz", 748, Arrays.asList(new String[] { "ios" })),
				createCountry("Sweden", "se", "swe", 752, Arrays.asList(new String[] { "ios" })),
				createCountry("Switzerland", "ch", "che", 756, Arrays.asList(new String[] { "ios" })), createCountry("Syrian Arab Republic", "sy", "syr", 760),
				createCountry("Taiwan, Province of China", "tw", "twn", 158, Arrays.asList(new String[] { "ios" })),
				createCountry("Tajikistan", "tj", "tjk", 762, Arrays.asList(new String[] { "ios" })),
				createCountry("Tanzania, United Republic of", "tz", "tza", 834, Arrays.asList(new String[] { "ios" })),
				createCountry("Thailand", "th", "tha", 764, Arrays.asList(new String[] { "ios" })), createCountry("Timor-Leste", "tl", "tls", 626),
				createCountry("Togo", "tg", "tgo", 768), createCountry("Tokelau", "tk", "tkl", 772), createCountry("Tonga", "to", "ton", 776),
				createCountry("Trinidad and Tobago", "tt", "tto", 780, Arrays.asList(new String[] { "ios" })),
				createCountry("Tunisia", "tn", "tun", 788, Arrays.asList(new String[] { "ios" })),
				createCountry("Turkey", "tr", "tur", 792, Arrays.asList(new String[] { "ios" })),
				createCountry("Turkmenistan", "tm", "tkm", 795, Arrays.asList(new String[] { "ios" })),
				createCountry("Turks and Caicos Islands", "tc", "tca", 796, Arrays.asList(new String[] { "ios" })), createCountry("Tuvalu", "tv", "tuv", 798),
				createCountry("Uganda", "ug", "uga", 800, Arrays.asList(new String[] { "ios" })),
				createCountry("Ukraine", "ua", "ukr", 804, Arrays.asList(new String[] { "ios" })),
				createCountry("United Arab Emirates", "ae", "are", 784, Arrays.asList(new String[] { "ios" })),
				createCountry("United Kingdom", "gb", "gbr", 826, Arrays.asList(new String[] { "ios" })),
				createCountry("United States", "us", "usa", 840, Arrays.asList(new String[] { "ios" })),
				createCountry("United States Minor Outlying Islands", "um", "umi", 581),
				createCountry("Uruguay", "uy", "ury", 858, Arrays.asList(new String[] { "ios" })),
				createCountry("Uzbekistan", "uz", "uzb", 860, Arrays.asList(new String[] { "ios" })), createCountry("Vanuatu", "vu", "vut", 548),
				createCountry("Venezuela, Bolivarian Republic of", "ve", "ven", 862, Arrays.asList(new String[] { "ios" })),
				createCountry("Viet Nam", "vn", "vnm", 704, Arrays.asList(new String[] { "ios" })),
				createCountry("Virgin Islands, British", "vg", "vgb", 92, Arrays.asList(new String[] { "ios" })),
				createCountry("Virgin Islands, U.S.", "vi", "vir", 850), createCountry("Wallis and Futuna", "wf", "wlf", 876),
				createCountry("Western Sahara", "eh", "esh", 732), createCountry("Yemen", "ye", "yem", 887, Arrays.asList(new String[] { "ios" })),
				createCountry("Zambia", "zm", "zmb", 894), createCountry("Zimbabwe", "zw", "zwe", 716, Arrays.asList(new String[] { "ios" })));
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
