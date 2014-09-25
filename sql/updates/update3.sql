delimiter $$

USE `rio`$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Has Linked Account','Users with this permission will be able to perform action reserved to who has a linked account','HLA')$$

CREATE TABLE IF NOT EXISTS `simplemodelrun` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`feedfetchid` int(11) NOT NULL,
	`a` double DEFAULT NULL,
	`b` double DEFAULT NULL,
	`deleted` enum('y','n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
	PRIMARY KEY (`id`),
	KEY `indexfeedfetchid` (`feedfetchid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;