delimiter $$

USE `rio`$$

CREATE TABLE `event` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `type` enum('system','user','rank') COLLATE utf8mb4_unicode_ci DEFAULT 'system',
    `code` char(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description` varchar(4096) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `priority`  enum('critical','high','normal','low','debug') COLLATE utf8mb4_unicode_ci DEFAULT 'normal',
    `subject` text COLLATE utf8mb4_unicode_ci,
    `shortbody` text COLLATE utf8mb4_unicode_ci,
    `longbody` text COLLATE utf8mb4_unicode_ci,
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
    `causeid` int(11) NOT NULL,
    `subject` text COLLATE utf8mb4_unicode_ci,
    `body` text COLLATE utf8mb4_unicode_ci,
    `status` enum('sending', 'sent', 'read', 'failed') COLLATE utf8mb4_unicode_ci DEFAULT 'sending',
    `type` enum('email', 'text', 'push', 'internal') COLLATE utf8mb4_unicode_ci DEFAULT 'internal',
    `deleted` enum('y', 'n') COLLATE utf8mb4_unicode_ci DEFAULT 'n',
    PRIMARY KEY (`id`),
    KEY `index_causeid` (`causeid`),
    KEY `index_status` (`status`),
    KEY `index_type` (`type`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_unicode_ci$$

