delimiter $$

USE `rio`$$

CREATE TABLE `event` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `type` enum('system','user','rank') COLLATE utf8mb4_unicode_ci DEFAULT 'system',
    `code` char(3) COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description` varchar(4096) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `priority`  enum('critical','high','normal','low','debug') COLLATE utf8mb4_unicode_ci DEFAULT 'normal',
    `subject` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `shortbody` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `longbody` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `deleted` enum('y', 'n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
    PRIMARY KEY (`id`), 
    UNIQUE KEY `uniquecode` (`code`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_unicode_ci$$

CREATE TABLE `eventsubscription` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `userid` int(11) NOT NULL,
    `eventid` int(11) NOT NULL,
    `eavesdroppingid` int(11) DEFAULT NULL,
    `email` enum('critical','high','normal','low','debug') COLLATE utf8mb4_unicode_ci DEFAULT 'high',
    `text` enum('critical','high','normal','low','debug') COLLATE utf8mb4_unicode_ci DEFAULT 'critical',
    `push` enum('critical','high','normal','low','debug') COLLATE utf8mb4_unicode_ci DEFAULT 'normal',
    `notificationcenter` enum('critical','high','normal','low','debug') COLLATE utf8mb4_unicode_ci DEFAULT 'debug',
    `deleted` enum('y', 'n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
    PRIMARY KEY (`id`),
    KEY `index_userid` (`userid`),
    KEY `index_eventid` (`eventid`),
    KEY `index_eavesdroppingid` (`eavesdroppingid`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_unicode_ci$$

CREATE TABLE `notification` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `causeid` int(11) NULL,
	`eventid` int(11) NOT NULL,
	`userid` int (11) NOT NULL,
    `from` text COLLATE utf8mb4_unicode_ci,
    `subject` text COLLATE utf8mb4_unicode_ci,
    `body` text COLLATE utf8mb4_unicode_ci,
    `status` enum('sending', 'sent', 'read', 'failed') COLLATE utf8mb4_unicode_ci DEFAULT 'sending',
    `type` enum('email', 'text', 'push', 'internal') COLLATE utf8mb4_unicode_ci DEFAULT 'internal',
    `deleted` enum('y', 'n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
    PRIMARY KEY (`id`),
	KEY `index_userid` (`userid`),
    KEY `index_causeid` (`causeid`),
    KEY `index_status` (`status`),
    KEY `index_type` (`type`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_unicode_ci$$

INSERT INTO `event` (`type`, `code`, `name`, `description`,`longbody`, `subject`) VALUES 
('user','TRQ', 'beta: Thanks for request', 'Sent to users who fill in the request invite form' ,'Hi ${user.forename},\n\nWe have received your request to be invited to our private beta.\n\nWe are continuously reviewing applications. We are looking for experienced developers and publishers who have relevant transactional data to improve our statistical forecasting. If your application is accepted we’ll be in touch. Note that partners are required to link their iTunes Connect account (no SDK needed).\n\nWe are working hard to create a functional, relevant and beautiful service. We can’t wait to share it with you.\n\nStay tuned,\n\nThe Reflection Team\r\nwww.reflection.io','Thank you for requesting an invite'),
('user', 'SEL', 'beta: You have been selected', 'Sent to users who have been selected to take part in the private beta','Congratulations ${user.forename},\n\nYou have been selected to take part in our private beta.\n\nPlease click the link below to complete your registration and link your iTunes Connect account.\n\n${link}\n\nAs a private beta partner we want your feedback. Please help us shape Reflection to your liking - email us or visit our forum to tell us what you think. \n\nIf you have any questions or problems please email beta@reflection.io\n\nThe Reflection Team\nwww.reflection.io','Reflection.io private beta'),
('user','RPS', 'Reset password', 'Sends a link to reset a forgotten password','Hi ${user.forename},\n\nPlease click the link below to enter a new password.\n\n${link}','Forgotten password'),
('user','PAS','Changed password', 'Send to users when they have changed their password','Hi ${user.forename},\n\nYour password was just changed. If this was not you please get in touch beta@reflection.io\n\nThanks,\nThe Reflection Team','Your password has been changed'),
('user','WEL', 'beta: Welcome to reflection', 'Sent to users when they have set their passwords for the first time after being invited','Hi ${user.forename},\n\nYour account is now ready to use. Welcome to our private beta.\n\nTo login please visit www.reflection.com/#!login\n\nIf you have any questions or problems please email beta@reflection.io\n\nThe Reflection Team','Welcome to our closed beta')$$

drop table `emailtemplate`$$
