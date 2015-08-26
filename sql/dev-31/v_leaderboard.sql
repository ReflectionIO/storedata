DROP VIEW IF EXISTS leaderboard;
CREATE VIEW leaderboard AS 
SELECT 
	`r`.`id` AS `id`,
	`r`.`rank_fetch_id` AS `rank_fetch_id`,
	`r`.`position` AS `position`,
	`r`.`itemid` AS `itemid`,
	`r`.`price` AS `price`,
	`r`.`currency` AS `currency`,
	`r`.`revenue_universal` AS `revenue`,
	`r`.`downloads_universal` AS `downloads`,
	`rf`.`group_fetch_code` AS `group_fetch_code`,
	`rf`.`fetch_date` AS `fetch_date`,
	`rf`.`country` AS `country`,
	`rf`.`category` AS `category`,
	`rf`.`type` AS `type`,
	`rf`.`platform` AS `platform`
FROM
    rank_fetch rf 
    INNER JOIN `rank2` r ON (r.rank_fetch_id = rf.rank_fetch_id and fetch_time>'22:00');

DROP VIEW IF EXISTS leaderboard_am; 
CREATE VIEW leaderboard_am AS 
SELECT 
	`r`.`id` AS `id`,
	`r`.`rank_fetch_id` AS `rank_fetch_id`,
	`r`.`position` AS `position`,
	`r`.`itemid` AS `itemid`,
	`r`.`price` AS `price`,
	`r`.`currency` AS `currency`,
	`r`.`revenue_universal` AS `revenue`,
	`r`.`downloads_universal` AS `downloads`,
	`rf`.`group_fetch_code` AS `group_fetch_code`,
	`rf`.`fetch_date` AS `fetch_date`,
	`rf`.`country` AS `country`,
	`rf`.`category` AS `category`,
	`rf`.`type` AS `type`,
	`rf`.`platform` AS `platform`
FROM
    rank_fetch rf 
    INNER JOIN `rank2` r ON (r.rank_fetch_id = rf.rank_fetch_id and fetch_time<'22:00');
