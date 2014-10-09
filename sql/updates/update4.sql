delimiter $$

USE `rio`$$

ALTER TABLE `dataaccount` ADD COLUMN `active` ENUM('y','n') NULL DEFAULT 'y' AFTER `properties`$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage categories','Users with this permission will be able to perform actions on categories','MCA')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage data accounts','Can perform actions on data accounts','MDA')$$