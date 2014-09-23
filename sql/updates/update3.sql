delimiter $$

USE `rio`$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Has Linked Account','Users with this permission will be able to perform action reserved to who has a linked account','HLA')$$

CREATE TABLE IF NOT EXISTS `simplemodelrun` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`categoryid` int(11) NOT NULL DEFAULT '24',
	`country` char(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
	`store` char(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
	`code` bigint(20) unsigned DEFAULT NULL,
	`form` enum('tablet','other') COLLATE utf8mb4_unicode_ci DEFAULT 'other',
	`listtype` enum('free','paid','grossing') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
	`a` double DEFAULT NULL,
	`b` double DEFAULT NULL,
	`deleted` enum('y','n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
	PRIMARY KEY (`id`),
	KEY `indexcode` (`code`),
	KEY `indexcategoryid` (`categoryid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$