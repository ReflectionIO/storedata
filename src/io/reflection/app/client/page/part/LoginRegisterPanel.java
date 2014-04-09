//
//  LoginRegisterPanel.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 3 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 *
 */
public class LoginRegisterPanel extends Composite {

	private static LoginRegisterPanelUiBinder uiBinder = GWT.create(LoginRegisterPanelUiBinder.class);

	interface LoginRegisterPanelUiBinder extends UiBinder<Widget, LoginRegisterPanel> {}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public LoginRegisterPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
