delimiter $$

USE `rio`$$

ALTER TABLE `simplemodelrun` 
ADD COLUMN `astandarderror` DOUBLE NULL AFTER `b`,
ADD COLUMN `bstandarderror` DOUBLE NULL AFTER `astandarderror`,
ADD COLUMN `adjustedrsquared` DOUBLE NULL AFTER `bstandarderror`,
ADD COLUMN `regressionsumsquares` DOUBLE NULL AFTER `adjustedrsquared`;

