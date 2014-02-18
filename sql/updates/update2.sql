delimiter $$

USE `rio`$$

create table `resource` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`deleted` enum('y','n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
`name` varchar(1000) COLLATE utf8mb4_unicode_ci,
`type` enum ('image', 'googlecloudserviceimage', 'youtubevideo','html5video') default 'image',
`data` text COLLATE utf8mb4_unicode_ci,
`properties` text COLLATE utf8mb4_unicode_ci,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

CREATE TABLE `emailtemplate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` enum('y','n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
  `from` text COLLATE utf8mb4_unicode_ci,
  `body` text COLLATE utf8mb4_unicode_ci,
  `subject` text COLLATE utf8mb4_unicode_ci,
  `format` enum('plaintext','html','rtf') COLLATE utf8mb4_unicode_ci DEFAULT 'plaintext',
  `type` enum('system','promotional') COLLATE utf8mb4_unicode_ci DEFAULT 'promotional',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

INSERT INTO `emailtemplate` (`from`,`body`,`subject`,`format`,`type`) VALUES 
('hello@reflection.io (Reflection)','Thanks for registering ${user.name}. We are currently in closed beta and only authorising select accounts. We are working as hard as we can to bring our product to every one. Please bear with us.','Thanks for registering','plaintext','system'),
('hello@reflection.io (Reflection)','Welcome ${user.name}','Welcome to reflection.io, you have been authorised for the ','plaintext','system'),
('no-reply@reflection.io (Reflection)','You have forgotten your password to enter a new password please click the link below. ${resetlink}','Welcome to reflection.io','plaintext','system'), 
('no-reply@reflection.io (Reflection)','The password on your account has been changed. If are not responsible for the change, please report this immediately','Password changed','plaintext','system')$$

ALTER TABLE `user` ADD COLUMN `code` CHAR(36) NULL DEFAULT NULL AFTER `verified`;