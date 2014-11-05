delimiter $$

USE `rio`$$

ALTER TABLE `dataaccount` ADD COLUMN `active` ENUM('y','n') NULL DEFAULT 'y' AFTER `properties`$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage categories','Users with this permission will be able to perform actions on categories','MCA')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage data accounts','Can perform actions on data accounts','MDA')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage data account fetches','Can perform actions on data account fetches','MDF')$$

INSERT INTO `permission` (`name`,`description`,`code`) VALUES ('Manage simple model run','Can perform actions on simple model runs','MSM')$$

INSERT INTO `role` (`name`,`description`,`code`) VALUES ('test','Can perform test tasks','TST')$$

ALTER TABLE `userrole` ADD COLUMN `rolecode` VARCHAR(3) NULL DEFAULT NULL AFTER `roleid`$$

ALTER TABLE `userpermission` ADD COLUMN `permissioncode` VARCHAR(3) NULL DEFAULT NULL AFTER `permissionid`$$

ALTER TABLE `rolepermission` ADD COLUMN `rolecode` VARCHAR(3) NULL DEFAULT NULL AFTER `roleid`$$

ALTER TABLE `rolepermission` ADD COLUMN `permissioncode` VARCHAR(3) NULL DEFAULT NULL AFTER `permissionid`$$

UPDATE `userrole` SET `rolecode`='ADM' WHERE `roleid` = 1$$

UPDATE `userrole` SET `rolecode`='DEV' WHERE `roleid` = 2$$

UPDATE `userrole` SET `rolecode`='PRE' WHERE `roleid` = 3$$

UPDATE `userrole` SET `rolecode`='ALF' WHERE `roleid` = 4$$

UPDATE `userrole` SET `rolecode`='BT1' WHERE `roleid` = 5$$

UPDATE `userrole` SET `rolecode`='TST' WHERE `roleid` = 6$$

UPDATE `rolepermission` SET `rolecode`='ADM' WHERE `roleid` = 1$$

UPDATE `rolepermission` SET `rolecode`='DEV' WHERE `roleid` = 2$$

UPDATE `rolepermission` SET `rolecode`='PRE' WHERE `roleid` = 3$$

UPDATE `rolepermission` SET `rolecode`='ALF' WHERE `roleid` = 4$$

UPDATE `rolepermission` SET `rolecode`='BT1' WHERE `roleid` = 5$$

UPDATE `rolepermission` SET `rolecode`='TST' WHERE `roleid` = 6$$

UPDATE `userpermission` SET `permissioncode`='FRV' WHERE `permissionid` = 1$$

UPDATE `userpermission` SET `permissioncode`='MFF' WHERE `permissionid` = 2$$

UPDATE `userpermission` SET `permissioncode`='MUS' WHERE `permissionid` = 3$$

UPDATE `userpermission` SET `permissioncode`='MRL' WHERE `permissionid` = 4$$

UPDATE `userpermission` SET `permissioncode`='MPR' WHERE `permissionid` = 5$$

UPDATE `userpermission` SET `permissioncode`='MET' WHERE `permissionid` = 6$$

UPDATE `userpermission` SET `permissioncode`='MIT' WHERE `permissionid` = 7$$

UPDATE `userpermission` SET `permissioncode`='MBL' WHERE `permissionid` = 8$$

UPDATE `userpermission` SET `permissioncode`='BPT' WHERE `permissionid` = 9$$

UPDATE `userpermission` SET `permissioncode`='BPO' WHERE `permissionid` = 10$$

UPDATE `userpermission` SET `permissioncode`='BEO' WHERE `permissionid` = 11$$

UPDATE `userpermission` SET `permissioncode`='BDO' WHERE `permissionid` = 12$$

UPDATE `userpermission` SET `permissioncode`='BLO' WHERE `permissionid` = 13$$

UPDATE `userpermission` SET `permissioncode`='BPA' WHERE `permissionid` = 14$$

UPDATE `userpermission` SET `permissioncode`='BEA' WHERE `permissionid` = 15$$

UPDATE `userpermission` SET `permissioncode`='BDA' WHERE `permissionid` = 16$$

UPDATE `userpermission` SET `permissioncode`='BLA' WHERE `permissionid` = 17$$

UPDATE `userpermission` SET `permissioncode`='BEP' WHERE `permissionid` = 18$$

UPDATE `userpermission` SET `permissioncode`='BDP' WHERE `permissionid` = 19$$

UPDATE `userpermission` SET `permissioncode`='HLA' WHERE `permissionid` = 20$$

UPDATE `userpermission` SET `permissioncode`='MCA' WHERE `permissionid` = 21$$

UPDATE `userpermission` SET `permissioncode`='MDA' WHERE `permissionid` = 22$$

UPDATE `userpermission` SET `permissioncode`='MSM' WHERE `permissionid` = 23$$

UPDATE `rolepermission` SET `permissioncode`='FRV' WHERE `permissionid` = 1$$

UPDATE `rolepermission` SET `permissioncode`='MFF' WHERE `permissionid` = 2$$

UPDATE `rolepermission` SET `permissioncode`='MUS' WHERE `permissionid` = 3$$

UPDATE `rolepermission` SET `permissioncode`='MRL' WHERE `permissionid` = 4$$

UPDATE `rolepermission` SET `permissioncode`='MPR' WHERE `permissionid` = 5$$

UPDATE `rolepermission` SET `permissioncode`='MET' WHERE `permissionid` = 6$$

UPDATE `rolepermission` SET `permissioncode`='MIT' WHERE `permissionid` = 7$$

UPDATE `rolepermission` SET `permissioncode`='MBL' WHERE `permissionid` = 8$$

UPDATE `rolepermission` SET `permissioncode`='BPT' WHERE `permissionid` = 9$$

UPDATE `rolepermission` SET `permissioncode`='BPO' WHERE `permissionid` = 10$$

UPDATE `rolepermission` SET `permissioncode`='BEO' WHERE `permissionid` = 11$$

UPDATE `rolepermission` SET `permissioncode`='BDO' WHERE `permissionid` = 12$$

UPDATE `rolepermission` SET `permissioncode`='BLO' WHERE `permissionid` = 13$$

UPDATE `rolepermission` SET `permissioncode`='BPA' WHERE `permissionid` = 14$$

UPDATE `rolepermission` SET `permissioncode`='BEA' WHERE `permissionid` = 15$$

UPDATE `rolepermission` SET `permissioncode`='BDA' WHERE `permissionid` = 16$$

UPDATE `rolepermission` SET `permissioncode`='BLA' WHERE `permissionid` = 17$$

UPDATE `rolepermission` SET `permissioncode`='BEP' WHERE `permissionid` = 18$$

UPDATE `rolepermission` SET `permissioncode`='BDP' WHERE `permissionid` = 19$$

UPDATE `rolepermission` SET `permissioncode`='HLA' WHERE `permissionid` = 20$$

UPDATE `rolepermission` SET `permissioncode`='MCA' WHERE `permissionid` = 21$$

UPDATE `rolepermission` SET `permissioncode`='MDA' WHERE `permissionid` = 22$$

UPDATE `rolepermission` SET `permissioncode`='MSM' WHERE `permissionid` = 23$$

ALTER TABLE `userrole` MODIFY `roleid` int(11) NULL DEFAULT NULL$$

ALTER TABLE `userpermission` MODIFY `permissionid` int(11) NULL DEFAULT NULL$$

ALTER TABLE `rolepermission` MODIFY `roleid` int(11) NULL DEFAULT NULL$$

ALTER TABLE `rolepermission` MODIFY `permissionid` int(11) NULL DEFAULT NULL$$

ALTER TABLE `userrole` MODIFY `rolecode` VARCHAR(3) NOT NULL$$

ALTER TABLE `userpermission` MODIFY `permissioncode` VARCHAR(3) NOT NULL$$

ALTER TABLE `rolepermission` MODIFY `rolecode` VARCHAR(3) NOT NULL$$

ALTER TABLE `rolepermission` MODIFY `permissioncode` VARCHAR(3) NOT NULL$$

