delimiter $$

CREATE DATABASE `rio` /*!40100 DEFAULT CHARACTER SET utf8 */$$


CREATE TABLE `country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(1000) DEFAULT NULL,
  `a2code` char(2) DEFAULT NULL,
  `a3code` char(3) DEFAULT NULL,
  `ncode` int(11) DEFAULT NULL,
  `continent` varchar(1000) DEFAULT NULL,
  `stores` text,
  `deleted` enum('y','n') DEFAULT 'n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$



CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `externalid` varchar(4096) DEFAULT NULL,
  `internalid` int(11) DEFAULT NULL,
  `name` varchar(1000) DEFAULT NULL,
  `creatorname` varchar(1000) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `source` char(3) DEFAULT NULL,
  `added` datetime DEFAULT NULL,
  `type` varchar(1000) DEFAULT NULL,
  `currency` varchar(100) DEFAULT NULL,
  `properties` text,
  `deleted` enum('y','n') DEFAULT 'n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$



CREATE TABLE `rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `position` int(11) DEFAULT NULL,
  `grossingposition` int(11) DEFAULT NULL,
  `itemid` varchar(4096) DEFAULT NULL,
  `type` varchar(1000) DEFAULT NULL,
  `country` char(3) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `source` char(3) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `currency` varchar(100) DEFAULT NULL,
  `code` varchar(100) DEFAULT NULL,
  `deleted` enum('y','n') DEFAULT 'n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$



CREATE TABLE `store` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `a3code` char(3) DEFAULT NULL,
  `name` varchar(1000) DEFAULT NULL,
  `url` varchar(4096) DEFAULT NULL,
  `countries` text,
  `deleted` enum('y','n') DEFAULT 'n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `sup_application_iap` (
  `internalid` int(11) NOT NULL,
  `lastupdated` datetime DEFAULT NULL,
  `usesiap`	 enum('y','n') DEFAULT NULL,
  PRIMARY KEY (`internalid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

INSERT INTO `rio`.`country`(
`name`,
`a2code`,
`a3code`,
`ncode`,
`stores`)
VALUES
("Afghanistan", "af", "afg", 4, ""), 
("Åland Islands", "ax", "ala", 248, ""), 
("Albania", "al", "alb", 8, "ios"),
("Algeria", "dz", "dza", 12, "ios"), 
("American Samoa", "as", "asm", 16, ""), 
("Andorra", "ad", "and", 20, ""), 
("Angola", "ao", "ago", 24, "ios"),
("Anguilla", "ai", "aia", 660, "ios"), 
("Antarctica", "aq", "ata", 10, ""), 
("Antigua and Barbuda", "ag", "atg", 28, "ios"),
("Argentina", "ar", "arg", 32, "ios"),
("Armenia", "am", "arm", 51, "ios"), 
("Aruba", "aw", "abw", 533, ""), 
("Australia", "au", "aus", 36, "ios"),
("Austria", "at", "aut", 40, "ios"),
("Azerbaijan", "az", "aze", 31, "ios"),
("Bahamas", "bs", "bhs", 44, "ios"),
("Bahrain", "bh", "bhr", 48, "ios"), 
("Bangladesh", "bd", "bgd", 50, ""), 
("Barbados", "bb", "brb", 52, "ios"),
("Belarus", "by", "blr", 112, "ios"),
("Belgium", "be", "bel", 56, "ios"),
("Belize", "bz", "blz", 84, "ios"),
("Benin", "bj", "ben", 204, "ios"),
("Bermuda", "bm", "bmu", 60, "ios"),
("Bhutan", "bt", "btn", 64, "ios"),
("Bolivia, Plurinational State of", "bo", "bol", 68, "ios"),
("Bonaire, Sint Eustatius and Saba", "bq", "bes", 535, ""), 
("Bosnia and Herzegovina", "ba", "bih", 70, ""), 
("Botswana", "bw", "bwa", 72, "ios"), 
("Bouvet Island", "bv", "bvt", 74, ""), 
("Brazil", "br", "bra", 76, "ios"),
("British Indian Ocean Territory", "io", "iot", 86, ""), 
("Brunei Darussalam", "bn", "brn", 96, "ios"),
("Bulgaria", "bg", "bgr", 100, "ios"),
("Burkina Faso", "bf", "bfa", 854, "ios"), 
("Burundi", "bi", "bdi", 108, ""), 
("Cambodia", "kh", "khm", 116, "ios"), 
("Cameroon", "cm", "cmr", 120, ""), 
("Canada", "ca", "can", 124, "ios"),
("Cape Verde", "cv", "cpv", 132, "ios"),
("Cayman Islands", "ky", "cym", 136, "ios"),
("Central African Republic", "cf", "caf", 140, ""), 
("Chad", "td", "tcd", 148, "ios"),
("Chile", "cl", "chl", 152, "ios"),
("China", "cn", "chn", 156, "ios"), 
("Christmas Island", "cx", "cxr", 162, ""),
("Cocos (Keeling) Islands", "cc", "cck", 166, ""), 
("Colombia", "co", "col", 170, "ios"),
("Comoros", "km", "com", 174, ""), 
("Congo", "cg", "cog", 178, "ios"),
("Congo, the Democratic Republic of the", "cd", "cod", 180, ""), 
("Cook Islands", "ck", "cok", 184, ""),
("Costa Rica", "cr", "cri", 188, "ios"), 
("Côte d'Ivoire", "ci", "civ", 384, ""),
("Croatia", "hr", "hrv", 191, "ios"), 
("Cuba", "cu", "cub", 192, ""),
("Curaçao", "cw", "cuw", 531, ""), 
("Cyprus", "cy", "cyp", 196, "ios"),
("Czech Republic", "cz", "cze", 203, "ios"),
("Denmark", "dk", "dnk", 208, "ios"), 
("Djibouti", "dj", "dji", 262, ""),
("Dominica", "dm", "dma", 212, "ios"),
("Dominican Republic", "do", "dom", 214, "ios"),
("Ecuador", "ec", "ecu", 218, "ios"),
("Egypt", "eg", "egy", 818, "ios"),
("El Salvador", "sv", "slv", 222, "ios"), 
("Equatorial Guinea", "gq", "gnq", 226, ""),
("Eritrea", "er", "eri", 232, ""), 
("Estonia", "ee", "est", 233, "ios"),
("Ethiopia", "et", "eth", 231, ""), 
("Falkland Islands (Malvinas)", "fk", "flk", 238, ""),
("Faroe Islands", "fo", "fro", 234, ""), 
("Fiji", "fj", "fji", 242, "ios"),
("Finland", "fi", "fin", 246, "ios"),
("France", "fr", "fra", 250, "ios"), 
("French Guiana", "gf", "guf", 254, ""),
("French Polynesia", "pf", "pyf", 258, ""),
("French Southern Territories", "tf", "atf", 260, ""),
("Gabon", "ga", "gab", 266, ""), 
("Gambia", "gm", "gmb", 270, "ios"),
("Georgia", "ge", "geo", 268, ""),
("Germany", "de", "deu", 276, "ios"),
("Ghana", "gh", "gha", 288, "ios"), 
("Gibraltar", "gi", "gib", 292, ""),
("Greece", "gr", "grc", 300, "ios"), 
("Greenland", "gl", "grl", 304, ""),
("Grenada", "gd", "grd", 308, "ios"), 
("Guadeloupe", "gp", "glp", 312, ""),
("Guam", "gu", "gum", 316, ""), 
("Guatemala", "gt", "gtm", 320, "ios"),
("Guernsey", "gg", "ggy", 831, ""), 
("Guinea", "gn", "gin", 324, ""),
("Guinea-Bissau", "gw", "gnb", 624, "ios"),
("Guyana", "gy", "guy", 328, "ios"), 
("Haiti", "ht", "hti", 332, ""),
("Heard Island and McDonald Islands", "hm", "hmd", 334, ""), 
("Holy See (Vatican City State)", "va", "vat", 336, ""),
("Honduras", "hn", "hnd", 340, "ios"),
("Hong Kong", "hk", "hkg", 344, "ios"),
("Hungary", "hu", "hun", 348, "ios"),
("Iceland", "is", "isl", 352, "ios"),
("India", "in", "ind", 356, "ios"),
("Indonesia", "id", "idn", 360, "ios"),
("Iran, Islamic Republic of", "ir", "irn", 364, ""), 
("Iraq", "iq", "irq", 368, ""),
("Ireland", "ie", "irl", 372, "ios"), 
("Isle of Man", "im", "imn", 833, ""),
("Israel", "il", "isr", 376, "ios"),
("Italy", "it", "ita", 380, "ios"),
("Jamaica", "jm", "jam", 388, "ios"),
("Japan", "jp", "jpn", 392, "ios"), 
("Jersey", "je", "jey", 832, ""),
("Jordan", "jo", "jor", 400, "ios"),
("Kazakhstan", "kz", "kaz", 398, "ios"),
("Kenya", "ke", "ken", 404, "ios"), 
("Kiribati", "ki", "kir", 296, ""),
("Korea, Democratic People's Republic of", "kp", "prk", 408, ""),
("Korea, Republic of", "kr", "kor", 410, "ios"),
("Kuwait", "kw", "kwt", 414, "ios"),
("Kyrgyzstan", "kg", "kgz", 417, "ios"),
("Lao People's Democratic Republic", "la", "lao", 418, "ios"),
("Latvia", "lv", "lva", 428, "ios"),
("Lebanon", "lb", "lbn", 422, "ios"), 
("Lesotho", "ls", "lso", 426, ""),
("Liberia", "lr", "lbr", 430, "ios"), 
("Libya", "ly", "lby", 434, ""),
("Liechtenstein", "li", "lie", 438, ""),
("Lithuania", "lt", "ltu", 440, "ios"),
("Luxembourg", "lu", "lux", 442, "ios"),
("Macao", "mo", "mac", 446, "ios"),
("Macedonia, The Former Yugoslav Republic of", "mk", "mkd", 807, "ios"),
("Madagascar", "mg", "mdg", 450, "ios"),
("Malawi", "mw", "mwi", 454, "ios"),
("Malaysia", "my", "mys", 458, "ios"), 
("Maldives", "mv", "mdv", 462, ""),
("Mali", "ml", "mli", 466, "ios"),
("Malta", "mt", "mlt", 470, "ios"), 
("Marshall Islands", "mh", "mhl", 584, ""),
("Martinique", "mq", "mtq", 474, ""), 
("Mauritania", "mr", "mrt", 478, "ios"),
("Mauritius", "mu", "mus", 480, "ios"), 
("Mayotte", "yt", "myt", 175, ""),
("Mexico", "mx", "mex", 484, "ios"),
("Micronesia, Federated States of", "fm", "fsm", 583, "ios"),
("Moldova, Republic of", "md", "mda", 498, "ios"), 
("Monaco", "mc", "mco", 492, ""),
("Mongolia", "mn", "mng", 496, "ios"), 
("Montenegro", "me", "mne", 499, ""),
("Montserrat", "ms", "msr", 500, "ios"), 
("Morocco", "ma", "mar", 504, ""),
("Mozambique", "mz", "moz", 508, "ios"), 
("Myanmar", "mm", "mmr", 104, ""),
("Namibia", "na", "nam", 516, "ios"), 
("Nauru", "nr", "nru", 520, ""),
("Nepal", "np", "npl", 524, "ios"),
("Netherlands", "nl", "nld", 528, "ios"), 
("New Caledonia", "nc", "ncl", 540, ""),
("New Zealand", "nz", "nzl", 554, "ios"),
("Nicaragua", "ni", "nic", 558, "ios"),
("Niger", "ne", "ner", 562, "ios"),
("Nigeria", "ng", "nga", 566, "ios"), 
("Niue", "nu", "niu", 570, ""),
("Norfolk Island", "nf", "nfk", 574, ""), 
("Northern Mariana Islands", "mp", "mnp", 580, ""),
("Norway", "no", "nor", 578, "ios"),
("Oman", "om", "omn", 512, "ios"),
("Pakistan", "pk", "pak", 586, "ios"),
("Palau", "pw", "plw", 585, "ios"), 
("Palestine, State of", "ps", "pse", 275, ""),
("Panama", "pa", "pan", 591, "ios"),
("Papua New Guinea", "pg", "png", 598, "ios"),
("Paraguay", "py", "pry", 600, "ios"),
("Peru", "pe", "per", 604, "ios"),
("Philippines", "ph", "phl", 608, "ios"), 
("Pitcairn", "pn", "pcn", 612, ""),
("Poland", "pl", "pol", 616, "ios"),
("Portugal", "pt", "prt", 620, "ios"), 
("Puerto Rico", "pr", "pri", 630, ""),
("Qatar", "qa", "qat", 634, "ios"), 
("Réunion", "re", "reu", 638, ""),
("Romania", "ro", "rou", 642, "ios"),
("Russian Federation", "ru", "rus", 643, "ios"), 
("Rwanda", "rw", "rwa", 646, ""),
("Saint Barthélemy", "bl", "blm", 652, ""), 
("Saint Helena, Ascension and Tristan da Cunha", "sh", "shn", 654, ""),
("Saint Kitts and Nevis", "kn", "kna", 659, "ios"),
("Saint Lucia", "lc", "lca", 662, "ios"),
("Saint Martin (French part)", "mf", "maf", 663, ""), 
("Saint Pierre and Miquelon", "pm", "spm", 666, ""),
("Saint Vincent and the Grenadines", "vc", "vct", 670, "ios"),
("Samoa", "ws", "wsm", 882, ""), 
("San Marino", "sm", "smr", 674, ""),
("Sao Tome and Principe", "st", "stp", 678, "ios"),
("Saudi Arabia", "sa", "sau", 682, "ios"),
("Senegal", "sn", "sen", 686, "ios"), 
("Serbia", "rs", "srb", 688, ""),
("Seychelles", "sc", "syc", 690, "ios"),
("Sierra Leone", "sl", "sle", 694, "ios"),
("Singapore", "sg", "sgp", 702, "ios"),
("Sint Maarten (Dutch part)", "sx", "sxm", 534, ""),
("Slovakia", "sk", "svk", 703, "ios"),
("Slovenia", "si", "svn", 705, "ios"),
("Solomon Islands", "sb", "slb", 90, "ios"), 
("Somalia", "so", "som", 706, ""),
("South Africa", "za", "zaf", 710, "ios"),
("South Georgia and the South Sandwich Islands", "gs", "sgs", 239, ""),
("South Sudan", "ss", "ssd", 728, ""),
("Spain", "es", "esp", 724, "ios"),
("Sri Lanka", "lk", "lka", 144, "ios"), 
("Sudan", "sd", "sdn", 729, ""),
("Suriname", "sr", "sur", 740, "ios"), 
("Svalbard and Jan Mayen", "sj", "sjm", 744, ""),
("Swaziland", "sz", "swz", 748, "ios"),
("Sweden", "se", "swe", 752, "ios"),
("Switzerland", "ch", "che", 756, "ios"), 
("Syrian Arab Republic", "sy", "syr", 760, ""),
("Taiwan, Province of China", "tw", "twn", 158, "ios"),
("Tajikistan", "tj", "tjk", 762, "ios"),
("Tanzania, United Republic of", "tz", "tza", 834, "ios"),
("Thailand", "th", "tha", 764, "ios"), 
("Timor-Leste", "tl", "tls", 626, ""),
("Togo", "tg", "tgo", 768, ""),
("Tokelau", "tk", "tkl", 772, ""), 
("Tonga", "to", "ton", 776, ""),
("Trinidad and Tobago", "tt", "tto", 780, "ios"),
("Tunisia", "tn", "tun", 788, "ios"),
("Turkey", "tr", "tur", 792, "ios"),
("Turkmenistan", "tm", "tkm", 795, "ios"),
("Turks and Caicos Islands", "tc", "tca", 796, "ios"), 
("Tuvalu", "tv", "tuv", 798, ""),
("Uganda", "ug", "uga", 800, "ios"),
("Ukraine", "ua", "ukr", 804, "ios"),
("United Arab Emirates", "ae", "are", 784, "ios"),
("United Kingdom", "gb", "gbr", 826, "ios"),
("United States", "us", "usa", 840, "ios"),
("United States Minor Outlying Islands", "um", "umi", 581, ""),
("Uruguay", "uy", "ury", 858, "ios"),
("Uzbekistan", "uz", "uzb", 860, "ios"), 
("Vanuatu", "vu", "vut", 548, ""),
("Venezuela, Bolivarian Republic of", "ve", "ven", 862, "ios"),
("Viet Nam", "vn", "vnm", 704, "ios"),
("Virgin Islands, British", "vg", "vgb", 92, "ios"),
("Virgin Islands, U.S.", "vi", "vir", 850, ""), 
("Wallis and Futuna", "wf", "wlf", 876, ""),
("Western Sahara", "eh", "esh", 732, ""), 
("Yemen", "ye", "yem", 887, "ios"),
("Zambia", "zm", "zmb", 894, ""), 
("Zimbabwe", "zw", "zwe", 716, "ios")$$

INSERT INTO `rio`.`store`
(`name`,
`a3code`,
`url`,
`countries`)
VALUES
("Apple iOS Store","ios","http://www.apple.com", "al,dz,ao,ai,ag,ar,am,au,at,az,bs,bh,bb,by,be,bz,bj,bm,bt,bo,bw,br,bn,bg,bf,kh,ca,cv,ky,td,cl,cn,co,cg,cr,hr,cy,cz,dk,dm,do,ec,eg,sv,ee,fj,fi,fr,gm,de,gh,gr,gd,gt,gw,gy,hn,hk,hu,is,in,id,ie,il,it,jm,jp,jo,kz,ke,kr,kw,kg,la,lv,lb,lr,lt,lu,mo,mk,mg,mw,my,ml,mt,mr,mu,mx,fm,md,mn,ms,mz,na,np,nl,nz,ni,ne,ng,no,om,pk,pw,pa,pg,py,pe,ph,pl,pt,qa,ro,ru,st,sa,sn,sc,sl,sg,sk,si,sb,za,es,lk,kn,lc,vc,sr,sz,se,ch,tw,tj,tz,th,tt,tn,tr,tm,tc,ug,gb,ua,ae,uy,us,uz,ve,vn,vg,ye,zw"),
("Apple Mac OS X Store", "mac", "http://www.apple.com", ""),
("Android Play Store", "gpl", "http://play.google.com", ""),
("Amazon Appstore for Android", "azn", "http://www.amazon.com", "")$$