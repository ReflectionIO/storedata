//
//  ProductPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 14 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ProductPage extends Page {

	private static ProductPageUiBinder uiBinder = GWT.create(ProductPageUiBinder.class);

	interface ProductPageUiBinder extends UiBinder<Widget, ProductPage> {}

	public ProductPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
