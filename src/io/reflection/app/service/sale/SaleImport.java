//
//  Created by AltiMario on 21/gen/2014.
//  Copyright © 2014 Reflection LTD. All rights reserved.
//
package io.reflection.app.service.sale;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

//
//  Created by AltiMario on 21/gen/2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
public class SaleImport {

	private List<List<String>> gZippedCSV2List(String csvZipFile, String token) throws Exception {

		String line = null;
		BufferedReader stream = null;
		List<List<String>> csvData = null;
		try {
			GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(csvZipFile));
			BufferedReader csvFile = new BufferedReader(new InputStreamReader(gzip));
			csvData = new ArrayList<List<String>>();
			stream = new BufferedReader(csvFile);
			while ((line = stream.readLine()) != null) {
				csvData.add(Arrays.asList(line.split(token)));
			}
		} finally {
			if (stream != null) stream.close();
		}
		return csvData;
	}

	private List<Sale> fromCVS2ListSale(List<List<String>> cvsDataList) throws Exception {

		List<Sale> listSale = new ArrayList<Sale>();
		List<String> line = null;
		int i = 1; // the first row is dropped because header
		while (i < cvsDataList.size()) {
			Sale sale = new Sale();			
			java.util.Date date = new java.util.Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			String formattedDate = df.format(date);

			line = cvsDataList.get(i);
			sale.id = (long) 1; 							// TODO: temporary, I don't know maybe it's autoincrement
			sale.item = new Item(); 						// TODO: temporary, I don't know if it's the right data
			sale.item.id = (long) 1;						// TODO: temporary, I don't know where is this data
			sale.created = df.parse(formattedDate);			// TODO: is it ok the current date?
			sale.account = new DataAccount(); 				// TODO: temporary, I don't know if it's the right data
			sale.deleted = "n";
			sale.country = line.get(1);
			sale.sku = line.get(2);
			sale.developer = line.get(3);
			sale.title = line.get(4);
			sale.version = line.get(5);
			sale.typeIdentifier = line.get(6);
			sale.units = line.get(7);
			// sale.proceeds=Integer.parseInt(line.get(8)); //is it an integer?
			sale.proceeds = 1; 								// TODO temporary
			sale.begin = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH).parse(line.get(9));
			sale.end = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH).parse(line.get(10));
			sale.currency = line.get(11);
			// sale.xxx=line.get(12); 						//TODO temporary, it's the country code but the data are not present in the db
			sale.customerCurrency = line.get(13); // TODO temporary, I'm not sure
			sale.account.id = Long.parseLong(line.get(14)); //TODO: I supposed is "Apple Identifier"
			// sale.customerPrice=Integer.parseInt(line.get(15)); //is the number integer?
			sale.customerPrice = 1; 						// TODO temporary
			sale.promoCode = line.get(15);
			sale.parentIdentifier = line.get(17);
			sale.subscription = line.get(18);
			sale.period = line.get(19);
			sale.category = line.get(20);
			i++;
			listSale.add(sale);
		}

		return listSale;
	}

	private String composeQueryInsertSale(List<Sale> saleList) {
		Sale sale = new Sale();
		int i = 0;
		String queryHeader = "INSERT INTO `sale` (`id`, `created`, `dataaccountid`, `itemid`, `country`, `sku`, `developer`, `title`, `version`, `typeidentifier`, `units`, `proceeds`, `currency`, `begin`, `end`, `customercurrency`, `customerprice`, `promocode`, `parentidentifier`, `subscription`, `period`, `category`, `deleted`) VALUES ";
		String queryTemplate = "(%d, FROM_UNIXTIME(%d),'%d','%d','%s','%s','%s','%s','%s','%s','%s',%d,'%s',FROM_UNIXTIME(%d),FROM_UNIXTIME(%d),'%s',%d,'%s','%s','%s','%s','%s','%s'),";
		String queryValues = "";
		while (i < saleList.size()) {
			sale = saleList.get(i);
			String tempStr = String.format(queryTemplate, 
					sale.id.longValue(), 
					sale.created.getTime() / 1000, 
					sale.account.id.longValue(), 
					sale.item.id.longValue(),
					addslashes(sale.country), 
					addslashes(sale.sku), 
					addslashes(sale.developer), 
					addslashes(sale.title), 
					addslashes(sale.version),
					addslashes(sale.typeIdentifier), 
					addslashes(sale.units), 
					sale.proceeds.intValue(), 
					addslashes(sale.currency), 
					sale.begin.getTime() / 1000,
					sale.end.getTime() / 1000, 
					addslashes(sale.customerCurrency), 
					sale.customerPrice.intValue(), 
					addslashes(sale.promoCode),
					addslashes(sale.parentIdentifier), 
					addslashes(sale.subscription), 
					addslashes(sale.period), 
					addslashes(sale.category),
					addslashes(sale.deleted));

			queryValues += tempStr;			
			i++;
		}
		return queryHeader + queryValues.substring(0, queryValues.length() - 1) + ";";
	}

}
