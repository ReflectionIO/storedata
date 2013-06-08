/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author William Shakour
 *
 */
public class DataCollectorAmazon extends DataStoreDataCollector implements DataCollector {

	private static final Logger LOG = Logger.getLogger(DataCollectorAmazon.class.getName());
	
	@Override
	public void enqueueCountriesAndTypes() {
		
	}
	
	@Override
	public void collect(String country, String type) {
//		<?php
//
//				//Enter your IDs
//				define("Access_Key_ID", "[Your Access Key ID]");
//				define("Associate_tag", "[Your Associate Tag ID]");
//
//				//Set up the operation in the request
//				function ItemSearch($SearchIndex, $Keywords){
//
//				//Set the values for some of the parameters
//				$Operation = "ItemSearch";
//				$Version = "2011-08-01";
//				$ResponseGroup = "ItemAttributes,Offers";
//				//User interface provides values
//				//for $SearchIndex and $Keywords
//
//				//Define the request
//				$request=
//				     "http://webservices.amazon.com/onca/xml"
//				   . "?Service=AWSECommerceService"
//				   . "&AssociateTag=" . Associate_tag
//				   . "&AWSAccessKeyId=" . Access_Key_ID
//				   . "&Operation=" . $Operation
//				   . "&Version=" . $Version
//				   . "&SearchIndex=" . $SearchIndex
//				   . "&Keywords=" . $Keywords
//				   . "&Signature=" . [Request Signature]
//				   . "&ResponseGroup=" . $ResponseGroup;
//
//				//Catch the response in the $response object
//				$response = file_get_contents($request);
//				$parsed_xml = simplexml_load_string($response);
//				printSearchResults($parsed_xml, $SearchIndex);
//				}
//				?>
		
		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Entering...");
		}
		
		try {
			
		} finally {
			if (LOG.isLoggable(Level.FINER)) {
				LOG.finer("Exiting...");
			}
		}
	}

}
