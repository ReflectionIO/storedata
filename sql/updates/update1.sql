delimiter $$

USE `rio`$$

ALTER TABLE `rank` ADD INDEX `indexcategoryid` (`categoryid` ASC)$$
ALTER TABLE `feedfetch` ADD INDEX `indexcategoryid` (`categoryid` ASC)$$

ALTER DATABASE `rio` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci$$

ALTER TABLE `category` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `country` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `dataaccount` CHANGE COLUMN `username` `username` VARCHAR(191) NOT NULL$$
ALTER TABLE `dataaccount` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `dataaccountfetch` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `datasource` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `feedfetch` DROP INDEX `indextype`,ADD INDEX `indextype` (`type`(191) ASC, `code` ASC)$$
ALTER TABLE `feedfetch` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `item` DROP INDEX `index_internalid`, ADD INDEX `indexinternalid` (`internalid` ASC), DROP INDEX `index_externalid`, ADD INDEX `indexexternalid` (`externalid`(191) ASC), CHANGE COLUMN `smallimage` `smallimage` TEXT NULL DEFAULT NULL, CHANGE COLUMN `mediumimage` `mediumimage` TEXT NULL DEFAULT NULL, CHANGE COLUMN `largeimage` `largeimage` TEXT NULL DEFAULT NULL$$
ALTER TABLE `item` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `modelrun` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `permission` DROP INDEX `index_name`, ADD INDEX `indexname` (`name` (191) ASC)$$
ALTER TABLE `permission` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `rank` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `role` DROP INDEX `index_name`, ADD INDEX `indexname` (`name` (191) ASC)$$
ALTER TABLE `role` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `rolepermission` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `sale` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `session` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `store` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `sup_application_iap` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `user` CHANGE COLUMN `username` `username` VARCHAR(191) NOT NULL, DROP INDEX `username`, ADD UNIQUE INDEX `uniqueusername` (`username` ASC)$$
ALTER TABLE `user` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `userdataaccount` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `userpermission` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `userrole` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `category` CHANGE `name` `name` VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `category` CHANGE `store` `store` CHAR(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `country` CHANGE `name` `name` VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `country` CHANGE `a2code` `a2code` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `country` CHANGE `a3code` `a3code` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `country` CHANGE `continent` `continent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `country` CHANGE `stores` `stores` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `dataaccount` CHANGE `username` `username` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `dataaccount` CHANGE `properties` `properties` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `dataaccountfetch` CHANGE `data` `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `datasource` CHANGE `a3code` `a3code` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `datasource` CHANGE `name` `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `datasource` CHANGE `url` `url` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `feedfetch` CHANGE `country` `country` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `feedfetch` CHANGE `store` `store` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `feedfetch` CHANGE `data` `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `feedfetch` CHANGE `code` `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `feedfetch` CHANGE `type` `type` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `item` CHANGE `externalid` `externalid` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `name` `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `creatorname` `creatorname` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `source` `source` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `type` `type` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `currency` `currency` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `smallimage` `smallimage` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `mediumimage` `mediumimage` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `largeimage` `largeimage` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `item` CHANGE `properties` `properties` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `modelrun` CHANGE `country` `country` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `modelrun` CHANGE `store` `store` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `modelrun` CHANGE `code` `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `permission` CHANGE `name` `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `permission` CHANGE `description` `description` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `permission` CHANGE `code` `code` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `rank` CHANGE `itemid` `itemid` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `rank` CHANGE `type` `type` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `rank` CHANGE `country` `country` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `rank` CHANGE `source` `source` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `rank` CHANGE `currency` `currency` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `rank` CHANGE `code` `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `role` CHANGE `name` `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `role` CHANGE `description` `description` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `role` CHANGE `code` `code` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `store` CHANGE `a3code` `a3code` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `store` CHANGE `name` `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `store` CHANGE `url` `url` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `store` CHANGE `countries` `countries` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

ALTER TABLE `user` CHANGE `forename` `forename` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `user` CHANGE `surname` `surname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `user` CHANGE `username` `username` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `user` CHANGE `avatar` `avatar` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `user` CHANGE `company` `company` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$
ALTER TABLE `user` CHANGE `password` `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci$$

