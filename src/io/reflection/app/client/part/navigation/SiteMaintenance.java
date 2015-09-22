//
//  SiteMaintenance.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 7 May 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class SiteMaintenance extends Composite {

	private static SiteMaintenanceUiBinder uiBinder = GWT.create(SiteMaintenanceUiBinder.class);

	interface SiteMaintenanceUiBinder extends UiBinder<Widget, SiteMaintenance> {}

	public SiteMaintenance() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pageMaintenance());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pageMaintenance());
	}

}
