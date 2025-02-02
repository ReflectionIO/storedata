delimiter $$

USE `rio`$$

ALTER TABLE `dataaccount` ADD COLUMN `active` ENUM('y','n') NULL DEFAULT 'y' AFTER `properties`$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage categories','Users with this permission will be able to perform actions on categories','MCA')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage data accounts','Can perform actions on data accounts','MDA')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage data account fetches','Can perform actions on data account fetches','MDF')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage simple model run','Can perform actions on simple model runs','MSM')$$

INSERT INTO `role` (`name`,`description`,`code`) VALUES ('test','Can perform test tasks','TST')$$