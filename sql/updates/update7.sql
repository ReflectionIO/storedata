delimiter $$

USE `rio`$$

-- if has been run exit without running

ALTER TABLE `rio`.`simplemodelrun` 
ADD COLUMN `summarydate` DATETIME NULL DEFAULT NULL AFTER `b`$$
