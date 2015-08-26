-- ====================================== RANK FETCH ========================================

DROP TABLE IF EXISTS `rank_fetch`;
CREATE TABLE `rank_fetch` (
  `rank_fetch_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_fetch_code` int(11) unsigned DEFAULT NULL,
  `fetch_date` date NOT NULL,
  `fetch_time` time NOT NULL,
  `country` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` int(11) DEFAULT NULL,
  `type` enum('FREE','PAID','GROSSING') COLLATE utf8_unicode_ci DEFAULT NULL,
  `platform` enum('PHONE','TABLET') COLLATE utf8_unicode_ci DEFAULT NULL,
  `url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_format` enum('JSON','GZIPPED_JSON','CSV','GZIPPED_CSV') COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` enum('GATHERING','GATHERED','INGESTING','INGESTED','MODELLING','MODELLED','PREDICTING','PREDICTED') COLLATE utf8_unicode_ci DEFAULT 'GATHERING',

  PRIMARY KEY (`rank_fetch_id`),

  KEY `idx_rank_fetch_type` (`type`),
  KEY `idx_rank_fetch_country` (`country`),
  KEY `idx_rank_fetch_category` (`category`),
  KEY `idx_rank_fetch_platform` (`platform`),
  KEY `idx_rank_fetch_group_fetch_code` (`group_fetch_code`),
  KEY `idx_rank_fetch_date` (`fetch_date`),
  KEY `idx_rank_fetch_status` (`status`),
  KEY `idx_rank_fetch_search_time` (`fetch_date`,`country`,`category`,`type`,`platform`),
  KEY `idx_rank_fetch_search_code` (`group_fetch_code`,`country`,`category`,`type`,`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



-- ====================================== RANK 2 ========================================

DROP TABLE IF EXISTS `rank2`;
CREATE TABLE `rank2` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rank_fetch_id` int(11) NOT NULL,
  `position` int(11) DEFAULT NULL,
  `itemid` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `currency` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,

  `revenue_iphone` bigint(20) DEFAULT NULL,
  `downloads_iphone` int(11) DEFAULT NULL,

  `revenue_ipad` bigint(20) DEFAULT NULL,
  `downloads_ipad` int(11) DEFAULT NULL,

  `revenue_universal` bigint(20) DEFAULT NULL,
  `downloads_universal` int(11) DEFAULT NULL,

  PRIMARY KEY (`id`),

  KEY `idx_rank_fetch_id` (`rank_fetch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- ====================================== Sale Summary ========================================

DROP TABLE IF EXISTS sale_summary;
CREATE TABLE `sale_summary` (
  `dataaccountid` int(11) NOT NULL DEFAULT '0',
  `date` date NOT NULL DEFAULT '0000-00-00',
  `itemid` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NULL',
  `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `country` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `currency` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `price` int(11) DEFAULT '0',

  `iphone_app_revenue` bigint(20) DEFAULT '0',
  `ipad_app_revenue` bigint(20) DEFAULT '0',
  `total_app_revenue` bigint(20) DEFAULT '0',

  `iphone_iap_revenue` bigint(20) DEFAULT '0',
  `ipad_iap_revenue` bigint(20) DEFAULT '0',
  `total_revenue` bigint(20) DEFAULT '0',

  `universal_app_revenue` bigint(20) DEFAULT '0',
  `iap_revenue` bigint(20) DEFAULT '0',

  `iphone_downloads` bigint(20) DEFAULT '0',
  `ipad_downloads` bigint(20) DEFAULT '0',
  `universal_downloads` bigint(20) DEFAULT '0',
  `total_downloads` bigint(20) DEFAULT '0',

  `iphone_updates` bigint(20) DEFAULT '0',
  `ipad_updates` bigint(20) DEFAULT '0',
  `universal_updates` bigint(20) DEFAULT '0',
  `total_updates` bigint(20) DEFAULT '0',

  `total_download_and_updates` bigint(20) DEFAULT '0',

  `free_subs_count` bigint(20) DEFAULT '0',
  `paid_subs_count` bigint(20) DEFAULT '0',
  `total_subs_count` bigint(20) DEFAULT '0',

  `subs_revenue` bigint(20) DEFAULT '0',

  PRIMARY KEY (`date`,`dataaccountid`,`itemid`,`country`),

  KEY `index_sales_summary_date` (`date`),
  KEY `index_sales_summary_country` (`country`),
  KEY `idx_item_search` (`date`,`country`,`itemid`),
  KEY `idx_dataaccount_item` (`dataaccountid`,`itemid`),
  KEY `idx_sales_summary_date_item` (`date`,`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- ====================================== Split data fetch ========================================

DROP TABLE IF EXISTS `split_data_fetch`;
CREATE TABLE `split_data_fetch` (
  `split_data_fetch_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_account_id` int(11) NOT NULL,

  `fetch_date` date NOT NULL,
  `fetch_time` time NOT NULL,

  `from_date` date DEFAULT NULL,
  `to_date` date DEFAULT NULL,

  `country` char(2) CHARACTER SET utf8mb4 NOT NULL,
  `itemid` varchar(75) COLLATE utf8_unicode_ci DEFAULT NULL,

  `downloads_report_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sales_report_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `iap_report_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,

  `status` enum('GATHERING','GATHERED','INGESTED') COLLATE utf8_unicode_ci DEFAULT 'GATHERING',

  PRIMARY KEY (`split_data_fetch_id`),

  KEY `idx_split_data_fetch_country` (`country`),
  KEY `idx_split_data_fetch_itemid` (`itemid`),
  KEY `idx_split_data_fetch_date` (`fetch_date`),
  KEY `idx_split_data_fetch_from_date` (`from_date`),
  KEY `idx_split_data_fetch_to_date` (`to_date`),
  KEY `idx_split_data_fetch_search` (`from_date`,`to_date`,`country`,`itemid`,`split_data_fetch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



-- ====================================== Split data ratio ========================================

DROP TABLE IF EXISTS `split_data_ratio`;
CREATE TABLE `split_data_ratio` (
  `data_account_id` int(11) NOT NULL,
  `itemid` varchar(75) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `country` char(2) CHARACTER SET utf8mb4 NOT NULL,
  `date` date NOT NULL,

  `phone_revenue_ratio` double DEFAULT '0',
  `tablet_revenue_ratio` double DEFAULT '0',

  `phone_iap_revenue_ratio` double DEFAULT '0',
  `tablet_iap_revenue_ratio` double DEFAULT '0',

  `phone_downloads` int(11) DEFAULT '0',
  `tablet_downloads` int(11) DEFAULT '0',

  `total_downloads` int(11) DEFAULT '0',

  PRIMARY KEY (`data_account_id`,`itemid`,`country`,`date`),

  UNIQUE KEY `data_account_id` (`data_account_id`,`itemid`,`country`,`date`),
  KEY `idx_split_data_ratio_country` (`country`),
  KEY `idx_split_data_ratio_itemid` (`itemid`),
  KEY `idx_split_data_ratio_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
