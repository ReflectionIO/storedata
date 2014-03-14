//
//  ConcreteImageAndText.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.cell.content;


/**
 * @author billy1380
 *
 */
public class ConcreteImageAndText implements ImageAndText {

	private String text;
	private String imageStyleName;

	/**
	 * 
	 */
	public ConcreteImageAndText(String imageStyleName, String text) {
		this.imageStyleName = imageStyleName;
		this.text = text;
	}
	
	/* (non-Javadoc)
	 * @see io.reflection.app.client.cell.content.ImageAndText#getImage()
	 */
	@Override
	public String getImageStyleName() {
		return imageStyleName;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.client.cell.content.ImageAndText#getText()
	 */
	@Override
	public String getText() {
		return text;
	}

}
