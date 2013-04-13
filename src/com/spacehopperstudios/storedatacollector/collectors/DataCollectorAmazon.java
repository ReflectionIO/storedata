/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

import org.apache.log4j.Logger;

/**
 * @author William Shakour
 *
 */
public class DataCollectorAmazon extends DataStoreDataCollector implements DataCollector {

	private static final Logger LOG = Logger.getLogger(DataCollectorAmazon.class);
	
	@Override
	public boolean collect() {
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
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Entering...");
		}

		boolean success = false;
		
		try {
			
		} finally {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Exiting...");
			}
		}
		
		return success;
	}

}
