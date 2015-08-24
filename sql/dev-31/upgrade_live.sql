SET @startTime = NOW();

source logging_procedures.sql;

select "Recreating tables and procedures" as status, NOW();
source recreate_tables.sql;

select "Recreating sales summary procedures" as status, NOW();
source sale_summary_procedures.sql;

select "Migrating data" as status, NOW();
source migrate_data.sql;

select "Updating missing developer names" as status, NOW();
source update_missing_developer_names.sql

select "Creating leaderboard views" as status, NOW();
source v_leaderboard_sales.sql;
source v_leaderboard.sql;

SET @endTime = NOW();
select "DB migrated. All Done" as status, NOW(), TIME_TO_SEC(TIMEDIFF(@endTime, @startTime)) as timeTaken;
