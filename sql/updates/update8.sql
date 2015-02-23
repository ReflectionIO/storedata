delimiter $$

USE `rio`$$

-- if has been run exit without running

ALTER TABLE `rio`.`sale` 
ADD COLUMN `dataaccountfetchid` INT(11) NULL DEFAULT NULL AFTER `created`,
ADD INDEX `indexdataaccountfetchid` (`dataaccountfetchid` ASC)$$

UPDATE `sale` LEFT JOIN `dataaccountfetch` ON `dataaccountfetch`.`date`=`sale`.`begin` AND `sale`.`dataaccountid`=`dataaccountfetch`.`linkedaccountid`
SET `sale`.`dataaccountfetchid`=`dataaccountfetch`.`id`$$
