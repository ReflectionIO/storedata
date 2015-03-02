
delimiter $$

USE `rio`$$

ALTER TABLE `rio`.`post` 
ADD COLUMN `code` VARCHAR(45) NOT NULL DEFAULT '' AFTER `title`,
ADD INDEX `indexcode` (`code` ASC)$$

