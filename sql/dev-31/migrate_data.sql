
-- ================= PREPARE RANK =======================
select "Creating rank composite index" as status, NOW();
ALTER TABLE `rank` ADD INDEX `idx_composite` (`code2` ASC, `country`(2) ASC, `type`(30) ASC, `categoryid` ASC);

-- ================= PREPARE SALE =======================

select "Creating sale indices" as status, NOW();
ALTER TABLE `sale` 
 CHANGE COLUMN `typeidentifier` `typeidentifier` VARCHAR(3) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL ,
 DROP INDEX `indextypeidentifier` ,
 ADD INDEX `indextypeidentifier` (`typeidentifier` ASC),
 ADD INDEX `idx_sale_search` (`dataaccountid` ASC, `begin` ASC, `typeidentifier` ASC);

-- ================= EXPORT RANK FETCH =======================
select "Exporting rank fetch data from feedfetch table" as status, NOW();
select id, code2, DATE(date), TIME(date), country, categoryid, 
		  IF(type like "topgrossing%", "GROSSING", IF(type like "toppaid%", "PAID", "FREE")) as type, 
		  IF(type like "%ipad%", "TABLET", "PHONE") as platform, 
		  data, 'JSON',
		  UPPER(`status`)
		from feedfetch
		where code2 > 922
		into OUTFILE '/tmp/rank_fetch.txt';

-- ================= LOAD RANK FETCH =======================
select "Loading rank fetch data into rank fetch table" as status, NOW();
LOAD DATA INFILE '/tmp/rank_fetch.txt' into TABLE rank_fetch (rank_fetch_id, group_fetch_code, fetch_date, fetch_time, country, category, type, platform, url, data_format, status);


-- ================= EXPORT RANK =======================
select "Exporting rank data from rank table" as status, NOW();
select ff.id as feedfetchid, IFNULL(IF(r.type like '%grossing%', r.grossingposition, r.position), 0) as position, itemid, price, currency, revenue, downloads 
	from rank r
	inner join feedfetch ff on (r.code2=ff.code2 and r.country=ff.country and r.type=ff.type and r.categoryid=ff.categoryid)
	where r.code2>922
	INTO OUTFILE '/tmp/rank2.txt';

-- ================= LOAD RANK FETCH =======================
select "Loading rank data into rank2 table" as status, NOW();
LOAD DATA INFILE '/tmp/rank2.txt' INTO TABLE rank2(rank_fetch_id, position, itemid, price, currency, revenue_universal, downloads_universal);


-- ================= LOAD RANK FETCH =======================

select DATE('2014-06-24') into @minDate; 
select DATE(begin) from sale order by id desc limit 1 into @maxDate;
select CONCAT("Got min data: ", @minDate, ', max date: ', @maxDate, ' from sale table. Summarizing sales...') as status, NOW();
CALL repopulate_sale_summary(@minDate, @maxDate);

-- ================= EXPORT MIGRATED DATA INTO FILES =======================

select "Exporting the new rank_fetch table" as status, NOW();
SELECT * FROM rank_fetch INTO OUTFILE '/tmp/new_rank_fetch.txt';

select "Exporting the new rank2 table" as status, NOW();
SELECT * FROM rank2 INTO OUTFILE '/tmp/new_rank2.txt';

select "Exporting the new summary table" as status, NOW();
SELECT * FROM sale_summary INTO OUTFILE '/tmp/new_sale_summary.txt';