DROP PROCEDURE IF EXISTS repopulate_sale_summary;
DELIMITER $$
-- This procedure simply loops through the dates provided and calls repopulate_sale_summary_for_date for each date
CREATE PROCEDURE repopulate_sale_summary (IN fromDate DATE, IN toDate DATE)
BEGIN
	DECLARE currentDate, endDate DATE DEFAULT DATE(NOW());
    DECLARE startTime, endTime TIMESTAMP DEFAULT NULL;
    DECLARE totalDaysProcessed INT DEFAULT 0;
    DECLARE logResetCount INT DEFAULT 0;
    
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		CALL log(CONCAT("Got a sqlexception"));
        CALL show_log();
        RESIGNAL;
	END;

    IF fromDate < toDate THEN
		SET currentDate = fromDate;
        SET endDate = toDate;
	ELSE
		SET currentDate = toDate;
        SET endDate = toDate;
    END IF;

	CALL reset_log();

	SET startTime = NOW();    

	CALL log(CONCAT('Current Date: ', currentDate, '. End Date: ', endDate));

	WHILE(currentDate<=endDate) DO
		CALL log(CONCAT('Calling procedure to reprocess the sales summaries for ', currentDate));

		BEGIN
        	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
			BEGIN
				CALL log(CONCAT("Got a sqlexception while processing date ", currentDate));
                CALL show_log();
			END;

			CALL repopulate_sale_summary_for_date(currentDate);
            SET totalDaysProcessed = totalDaysProcessed + 1;
		END;
        
		SET currentDate = (SELECT ADDDATE(currentDate, 1));
        SET logResetCount = logResetCount + 1;
        
        IF logResetCount > 15 THEN
			SET logResetCount = 0;
            CALL show_log();
            CALL reset_log();
        END IF;
	END WHILE;
    
	SET endTime = NOW();

	CALL log(CONCAT('Total days processed: ', totalDaysProcessed));
	CALL log(CONCAT('Procedure running time was ', TIME_TO_SEC(TIMEDIFF(endTime, startTime)), ' seconds. Start time was ', startTime));

	CALL show_log();
END $$
DELIMITER ;



-- ************************************************************************************************************************************
-- ************************************************************************************************************************************
-- ***************************************        repopulate_sale_summary_for_date       **********************************************
-- ************************************************************************************************************************************
-- ************************************************************************************************************************************

DROP PROCEDURE IF EXISTS repopulate_sale_summary_for_date;
DELIMITER $$
-- This procedure loops through all data accounts that we have sales data for on the particular given date and calls repopulate_sale_summary_for_dataaccount_on_date
CREATE PROCEDURE repopulate_sale_summary_for_date (IN forDate DATE)
BEGIN
    DECLARE accountId, currentRow INT;
    DECLARE rowCountOfAccountsToProcess INT DEFAULT 0;

    DECLARE dataAccountWithSales CURSOR FOR 
		SELECT SQL_CALC_FOUND_ROWS distinct dataaccountid FROM sale WHERE begin = forDate;

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		CALL log(CONCAT("Got a sqlexception"));
        CALL show_log();
		CLOSE dataAccountWithSales;
        RESIGNAL;
	END;

	-- CALL log(CONCAT('Opening cursor on sale for distinct data accounts'));
	
	OPEN dataAccountWithSales;
	
	SET rowCountOfAccountsToProcess = (SELECT FOUND_ROWS());
	CALL log(CONCAT('Found ', rowCountOfAccountsToProcess, ' data accounts with sale entries on ', forDate));

	SET currentRow = 0;
		
	WHILE currentRow < rowCountOfAccountsToProcess DO
		FETCH dataAccountWithSales INTO accountId;
		
        CALL log(CONCAT('Calling repopulate_sale_summary_for_dataaccount_on_date for account ', accountId));
        CALL repopulate_sale_summary_for_dataaccount_on_date(accountId, forDate);
        
        SET currentRow = currentRow + 1;
        SET accountId = NULL;
	END WHILE;
    
    CLOSE dataAccountWithSales;

END $$
DELIMITER ;


-- ************************************************************************************************************************************
-- ************************************************************************************************************************************
-- ***********************************    repopulate_sale_summary_for_dataaccount_on_date    ******************************************
-- ************************************************************************************************************************************
-- ************************************************************************************************************************************

DROP PROCEDURE IF EXISTS repopulate_sale_summary_for_dataaccount_on_date;
DELIMITER $$
-- This procedure calculates the sale summary for all items of a data account for a given date
CREATE PROCEDURE repopulate_sale_summary_for_dataaccount_on_date (IN currentAccountId INT, IN forDate DATE)
BEGIN
	DECLARE vItemId, vTitle, vSKU, vCountry, vCurrency VARCHAR(1000) DEFAULT NULL;

    DECLARE vPrice bigint(20) DEFAULT 0;
    DECLARE iPhoneAppRevenue, iPadAppRevenue, uniAppRevenue, iapRevenue bigint(20) DEFAULT 0;
    DECLARE iPhoneDownloads, iPadDownloads, uniDownloads bigint(20) DEFAULT 0;
    DECLARE iPhoneUpdates, iPadUpdates, uniUpdates bigint(20) DEFAULT 0;
    DECLARE freeSubsCount, paidSubsCount, subsRevenue bigint(20) DEFAULT 0;

    DECLARE notFound BOOLEAN DEFAULT FALSE;
    DECLARE itemsFoundCount, currentItemNo INT DEFAULT 0;
    
	DECLARE appSaleOrDownloadCur CURSOR FOR 
        SELECT 
			SQL_CALC_FOUND_ROWS
			ds.itemid,
			ds.title,
			ds.sku,
			ds.country,
			
			ds.currency,
			MAX(ds.customerprice),

			SUM(IF(ds.typeidentifier = '1', (ABS(ds.units) * ds.customerprice), 0)), -- AS iphone_app_revenue,
			SUM(IF(ds.typeidentifier = '1T', (ABS(ds.units) * ds.customerprice), 0)), -- AS ipad_app_revenue,
			SUM(IF(ds.typeidentifier = '1F', (ABS(ds.units) * ds.customerprice), 0)), -- AS universal_app_revenue,
            
			SUM(IF(ds.typeidentifier = '1', ds.units, 0)), -- AS iphone_download,
			SUM(IF(ds.typeidentifier = '1T', ds.units, 0)), -- AS ipad_download,
			SUM(IF(ds.typeidentifier = '1F', ds.units, 0)), -- AS universal_download,

			SUM(IF(ds.typeidentifier = '7', ds.units, 0)), -- AS iphone_update,
			SUM(IF(ds.typeidentifier = '7T', ds.units, 0)), -- AS ipad_update,
			SUM(IF(ds.typeidentifier = '7F', ds.units, 0)) -- AS universal_update
		FROM sale ds
        where ds.dataaccountid=currentAccountId and ds.begin=forDate and ds.typeidentifier in ('1' , '1F', '1T', '7', '7T', '7F')
        group by ds.itemid, ds.country;

	DECLARE CONTINUE HANDLER FOR NOT FOUND 
    BEGIN
		CALL log('Not found handler invoked');
		SET notFound = TRUE;
	END;
    
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		CALL log(CONCAT("Got a sqlexception"));
        CALL show_log();
		-- CLOSE appSaleOrDownloadCur;
        RESIGNAL;
	END;

	CALL log(CONCAT('Deleting all records for ', forDate, ' from sale_summary table for data account ', currentAccountId));
	
	DELETE FROM sale_summary WHERE date=forDate AND dataaccountid=currentAccountId;
	
    CALL log(CONCAT('Deleted ', ROW_COUNT(), ' rows'));

	OPEN appSaleOrDownloadCur;
   	SET itemsFoundCount = (SELECT FOUND_ROWS());

    CALL log(CONCAT('Found ', itemsFoundCount, ' items sold/downloaded on ', forDate));

	SELECT  0,0,0,0,0,
			0,0,0,
			0,0,0,
			0,0,0 INTO
		vPrice, iPhoneAppRevenue, iPadAppRevenue, uniAppRevenue, iapRevenue, 
		iPhoneDownloads, iPadDownloads, uniDownloads, 
		iPhoneUpdates, iPadUpdates, uniUpdates,
		freeSubsCount, paidSubsCount, subsRevenue;


	WHILE currentItemNo < itemsFoundCount DO
		FETCH appSaleOrDownloadCur INTO vItemId, vTitle, vSKU, vCountry, vCurrency, vPrice, iPhoneAppRevenue, iPadAppRevenue, uniAppRevenue, iPhoneDownloads, iPadDownloads, uniDownloads, iPhoneUpdates, iPadUpdates, uniUpdates;


        IF ( NOT notFound ) THEN
        
			SELECT 
				SUM(x.iapRevenue), SUM(x.subsRevenue), SUM(x.paidSubsCount), SUM(x.freeSubsCount)
				INTO iapRevenue, subsRevenue, paidSubsCount, freeSubsCount
			FROM
				(SELECT 
					IF(iaps.typeidentifier = 'IA1', IFNULL(SUM(iaps.units * iaps.customerprice), 0), 0) as iapRevenue, -- iap revenue
					IF(iaps.typeidentifier IN ('IA9', 'IAY'), IFNULL(SUM(iaps.units * iaps.customerprice), 0), 0) as subsRevenue, -- paid subscription or auto renewal revenue
					IF(iaps.typeidentifier IN ('IA9', 'IAY'), IFNULL(SUM(iaps.units), 0), 0) as paidSubsCount, -- paid subscription or auto renewal count
					IF(iaps.typeidentifier = 'IAC', IFNULL(SUM(iaps.units), 0), 0) as freeSubsCount-- free subscription
				FROM
					sale iaps
				WHERE
					iaps.dataaccountid=currentAccountId
						AND iaps.begin = forDate
						AND iaps.typeidentifier in ('IA1', 'IA9', 'IAY', 'IAC')
						AND iaps.parentidentifier = vSKU
						AND iaps.country = vCountry
				GROUP BY iaps.typeidentifier) as x;

			IF ( notFound ) THEN
				CALL log(CONCAT('There was an error fetching the next IAP revenue for item number ', currentItemNo, ' for date ', forDate));
            END IF;        
			
            SET iPhoneAppRevenue = IFNULL(iPhoneAppRevenue, 0);
			SET iPadAppRevenue = IFNULL(iPadAppRevenue, 0);
			SET uniAppRevenue = IFNULL(uniAppRevenue, 0);
			SET iapRevenue = IFNULL(iapRevenue, 0);
			SET iPhoneDownloads = IFNULL(iPhoneDownloads, 0);
			SET iPadDownloads = IFNULL(iPadDownloads, 0);
			SET uniDownloads = IFNULL(uniDownloads, 0);
			SET iPhoneUpdates = IFNULL(iPhoneUpdates, 0);
			SET iPadUpdates = IFNULL(iPadUpdates, 0);
			SET uniUpdates	 = IFNULL(uniUpdates, 0);
            /*
            CALL log(CONCAT('INSERTING SUMMARY ROW: ', currentAccountId, ', ', forDate, ', ',  vItemId, ', ',  vTitle, ', ',  vCountry, ', ',  vCurrency, ', ',  IFNULL(vPrice, 'NULL'), ', ', 
					iPhoneAppRevenue, ', ', iPadAppRevenue, ', ', uniAppRevenue, ', ', iapRevenue, ', ',
                    iPhoneDownloads, ', ', iPadDownloads, ', ', uniDownloads, ', ',
                    iPhoneUpdates, ', ', iPadUpdates, ', ', uniUpdates ));
			*/
			INSERT INTO sale_summary 
				(`dataaccountid`, `date`, `itemid`, `title`, `country`, `currency`, `price`, 
				`iphone_app_revenue`, `ipad_app_revenue`, `universal_app_revenue`, `total_app_revenue`, `iap_revenue`, `total_revenue`,
				`iphone_downloads`, `ipad_downloads`, `universal_downloads`, `total_downloads`,
				`iphone_updates`, `ipad_updates`, `universal_updates`, `total_updates`, 
                `total_download_and_updates`, 
                `free_subs_count`, `paid_subs_count`, `total_subs_count`, `subs_revenue`)
			VALUES
				(currentAccountId, forDate, vItemId, vTitle, vCountry, vCurrency, vPrice, 
				iPhoneAppRevenue, iPadAppRevenue, uniAppRevenue, (iPhoneAppRevenue + iPadAppRevenue + uniAppRevenue), iapRevenue, (iPhoneAppRevenue + iPadAppRevenue + uniAppRevenue + iapRevenue), 
				iPhoneDownloads, iPadDownloads, uniDownloads, (iPhoneDownloads + iPadDownloads + uniDownloads),
				iPhoneUpdates, iPadUpdates, uniUpdates, (iPhoneUpdates + iPadUpdates + uniUpdates),
				(iPhoneDownloads + iPadDownloads + uniDownloads) + (iPhoneUpdates + iPadUpdates + uniUpdates),
                freeSubsCount, paidSubsCount, (freeSubsCount+paidSubsCount), subsRevenue);
		ELSE
			CALL log(CONCAT('There was an error fetching the next item summary for dataAccountId ', currentAccountId, ' for date ', forDate));
		END IF;

        SET vItemId = NULL;
        SET vTitle = NULL;
        SET vSKU = NULL;
        SET vCountry = NULL;
        SET vCurrency = NULL;
        SELECT  0,0,0,0,0,
				0,0,0,
                0,0,0,
                0,0,0 INTO
			vPrice, iPhoneAppRevenue, iPadAppRevenue, uniAppRevenue, iapRevenue, 
            iPhoneDownloads, iPadDownloads, uniDownloads, 
            iPhoneUpdates, iPadUpdates, uniUpdates,
            freeSubsCount, paidSubsCount, subsRevenue;

        SET notFound = FALSE;
        SET currentItemNo = currentItemNo + 1;

	END WHILE;
    
    -- need to process all items where iaps were sold but the parent items were not downloaded or sold
	CALL repopulate_sale_summary_for_iap_missing_parent_sales(currentAccountId, forDate);

	CLOSE appSaleOrDownloadCur;

END $$
DELIMITER ;



-- PROCESS IAPS with missing sales on the day


-- ************************************************************************************************************************************
-- ************************************************************************************************************************************
-- *********************************** repopulate_sale_summary_for_iap_missing_parent_sales *******************************************
-- ************************************************************************************************************************************
-- ************************************************************************************************************************************

DROP PROCEDURE IF EXISTS repopulate_sale_summary_for_iap_missing_parent_sales;
DELIMITER $$
-- This procedure 
CREATE PROCEDURE repopulate_sale_summary_for_iap_missing_parent_sales (IN vDataAccountId INT, IN forDate DATE)
BEGIN
    DECLARE currentRow INT;
    DECLARE rowCount INT DEFAULT 0;
    DECLARE notFound BOOLEAN DEFAULT FALSE;
    DECLARE hasError BOOLEAN DEFAULT FALSE;
    
    DECLARE vCountry CHAR(2);
    DECLARE vCurrency CHAR(3);
    DECLARE vSKU, vItemId, vTitle VARCHAR(255);
    DECLARE vPrice, vRevenue INT;

    DECLARE iapsPerCountryWithOutSales CURSOR FOR 
		SELECT iaps.country, iaps.parentidentifier, iaps.currency, SUM((ABS(iaps.units) * iaps.customerprice)) as revenue
		FROM sale iaps 
		WHERE
			iaps.dataaccountid=vDataAccountId
            AND iaps.begin=forDate
			AND (iaps.typeidentifier = 'IA1' OR iaps.typeidentifier = 'IA9')
			AND iaps.parentidentifier NOT IN
				-- ...that was not downloaded / sold on the same day as the iap
				(SELECT 
					DISTINCT dd.sku
				FROM 
					sale dd
				where 
					dd.dataaccountid=iaps.dataaccountid 
					AND dd.begin=iaps.begin 
                    AND dd.country=iaps.country
					AND dd.typeidentifier IN ('1' , '1F', '1T', '7', '7T', '7F')
				)
		group by iaps.country, iaps.parentidentifier;

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET notFound = TRUE;
    
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		CALL log(CONCAT("Got a sqlexception"));
        CALL show_log();
		CLOSE iapsPerCountryWithOutSales;
        RESIGNAL;
	END;

	-- CALL log(CONCAT('Opening cursor on iap skus per country that don't have a sale on the same day'));
	
	OPEN iapsPerCountryWithOutSales;
	
	SET rowCount = (SELECT FOUND_ROWS());
	CALL log(CONCAT('Found ', rowCount, ' iaps per country with no sale / download for it\'s parent item ', forDate));

	SET currentRow = 0;

	WHILE currentRow < rowCount DO
        FETCH iapsPerCountryWithOutSales INTO vCountry, vSKU, vCurrency, vRevenue;

        -- Get the latest sale for the parent item of this IAP in the same country (this is to get the customer price too)
        SELECT itemid, title, customerprice
          INTO vItemId, vTitle, vPrice
		FROM sale WHERE country=vCountry AND sku=vSKU ORDER BY id DESC LIMIT 1;
        
        IF ( (NOT notFound)  OR (vItemId IS NULL)) THEN
			-- CALL log(CONCAT('Could not find a purchase of this IAP''s parent item with sku ', vSKU, ' in this country ', vCountry));
			-- As there was no sale of the parent item in this country in our records, lets just get the title and itemid of the parent
            -- from the last sale in any country. We will set the customer price to NULL and just register the revenue in the IAP currency
			SET vPrice = NULL;
            
			SELECT itemid, title 
			  INTO vItemId, vTitle
			FROM sale WHERE sku=vSKU ORDER BY id DESC LIMIT 1;
        END IF;

		IF vItemId is NULL THEN
			CALL log(CONCAT('*** FATAL *** Got an IAP for which we don''t have a parent item at all. IAP parentidentifier: ', vSKU));
            SET hasError = TRUE;
		END IF;

		IF NOT hasError THEN
			-- CALL log(CONCAT('INSERTING IAP ROW: ', vDataAccountId, ', ', forDate, ', ',  vItemId, ', ',  vTitle, ', ',  vCountry, ', ',  vCurrency, ', ',  IFNULL(vPrice, 'NULL'), ', ',  vRevenue, ', ',  vRevenue));

			INSERT INTO sale_summary 
				(`dataaccountid`, `date`, `itemid`, `title`, `country`, `currency`, `price`, `iap_revenue`, `total_revenue`) 
			VALUES
				(vDataAccountId, forDate, vItemId, vTitle, vCountry, vCurrency, vPrice, vRevenue, vRevenue);
			
		END IF;
        
        SET currentRow = currentRow + 1;
        
        -- reset all variables to we don't misuse old values.
        SET vItemId = NULL;
        SET vTitle = NULL;
        SET vPrice = NULL;
        SET vCountry = NULL;
        SET vSKU = NULL;
        SET vCurrency = NULL;
        SET vRevenue = null;
        SET hasError = FALSE;
        SET notFound = FALSE;
        
	END WHILE;
    
    CLOSE iapsPerCountryWithOutSales;
END $$
DELIMITER ;

-- CALL repopulate_sale_summary('2014-06-25', '2014-06-25');

-- If the above is successfull then call the one below. Make sure you have created the logging procedures and the sales summary table.
-- CALL repopulate_sale_summary('2014-06-26', '2015-06-15');

-- SELECT * FROM sale_summary INTO OUTFILE '/tmp/sale_summary.txt';