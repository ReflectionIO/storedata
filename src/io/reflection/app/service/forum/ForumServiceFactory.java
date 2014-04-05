//  
//  ForumServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.forum;
final class ForumServiceFactory {

/**
* @return
*/
public static IForumService createNewForumService () {
return new ForumService();
}

}