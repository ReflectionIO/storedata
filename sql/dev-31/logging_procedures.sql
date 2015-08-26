DROP PROCEDURE IF EXISTS reset_log;
DELIMITER $$
CREATE PROCEDURE reset_log ()
BEGIN
    create table if not exists sp_log (
		log_time timestamp DEFAULT CURRENT_TIMESTAMP,
		msg varchar(512)
    ) engine = memory;
    
    truncate table sp_log;
END $$
DELIMITER ;

DROP PROCEDURE IF EXISTS log;
DELIMITER $$
CREATE PROCEDURE log (IN message varchar(512))
BEGIN
	
    insert into sp_log(msg) values (message);

END $$
DELIMITER ;

DROP PROCEDURE IF EXISTS show_log;
DELIMITER $$
CREATE PROCEDURE show_log ()
BEGIN
	
    select * from sp_log;

END $$
DELIMITER ;
