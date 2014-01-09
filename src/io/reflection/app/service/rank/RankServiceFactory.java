//  
//  RankServiceFactory.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

final class RankServiceFactory {

/**
* @return
*/
public static IRankService createNewRankService () {
return new RankService();
}

}