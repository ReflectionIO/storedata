delimiter $$

USE `rio`$$

INSERT INTO `rio`.`permission`
(`name`,`description`,`code`) VALUES
('Manage event subscriptions','Users with this permission will be able to perform actions on event subscriptions','MES'),
('Send notifications', 'Users with this permission will be able to send notifications','SNO')$$


UPDATE `rio`.`permission`
SET `name` = 'Manage events',`description` = 'Users with this permission will be able to perform actions on events'
WHERE `code` = 'MET'$$

