ALTER TABLE `dataaccount` ADD COLUMN `developer_name` VARCHAR(255) NULL AFTER `password`;

DROP PROCEDURE IF EXISTS update_missing_developer_names;
DELIMITER $$
CREATE PROCEDURE update_missing_developer_names ()
BEGIN
    DECLARE startTime, endTime TIMESTAMP DEFAULT NULL;
    DECLARE dataAccountID, currentRow INT;
    DECLARE rowCountOfAccountsToProcess INT DEFAULT 0;
    DECLARE developerName VARCHAR(1000);
    DECLARE totalAccountsUpdated INT DEFAULT 0;
    
    DECLARE dataaccountIdsWithMissingDeveloperNames CURSOR FOR SELECT SQL_CALC_FOUND_ROWS id FROM dataaccount WHERE developer_name is NULL AND deleted='n';
    
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		CALL log(CONCAT("Got a sqlexception"));
        CLOSE dataaccountIdsWithMissingDeveloperNames;
        RESIGNAL;
	END;

	CALL reset_log();

	SET startTime = NOW();    

	CALL log(CONCAT('Opening cursor on developer names missing from dataaccount'));
	
	OPEN dataaccountIdsWithMissingDeveloperNames;
	
	SET rowCountOfAccountsToProcess = (SELECT FOUND_ROWS());
	CALL log(CONCAT('Found ', rowCountOfAccountsToProcess, ' data accounts with no developer name'));

	SET currentRow = 0;
		
	WHILE currentRow < rowCountOfAccountsToProcess DO
		CALL log(CONCAT("Fetching row: ", currentRow));
		FETCH dataaccountIdsWithMissingDeveloperNames INTO dataAccountID;
		
		SELECT IFNULL(s.developer, '') INTO developerName FROM sale s WHERE s.dataaccountid=dataAccountID AND s.developer IS NOT NULL AND s.developer <> '' LIMIT 1;
		
 		IF NOT ISNULL(developerName) AND developerName <> '' THEN
			CALL log(CONCAT('Got developer name ', developerName, ' for dataaccount id: ', dataAccountID));
 			UPDATE dataaccount SET developer_name=developerName WHERE id=dataAccountID;
 			SET totalAccountsUpdated = totalAccountsUpdated + 1;
		ELSE
			CALL log(CONCAT('No developer name for dataaccount id: ', dataAccountID));
 		END IF;
		
        SET currentRow = currentRow + 1;
        SET dataAccountID = 0;
        SET developerName = NULL;
	END WHILE;

	SET endTime = NOW();

	CALL log(CONCAT('Total data accounts updated: ', totalAccountsUpdated));
	CALL log(CONCAT('Procedure running time was ', TIME_TO_SEC(TIMEDIFF(endTime, startTime)), ' seconds. Start time was ', startTime));

	CLOSE dataaccountIdsWithMissingDeveloperNames;

	CALL show_log();
END $$
DELIMITER ;

CALL update_missing_developer_names();
