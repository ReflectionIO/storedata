//
//  Images.java
//  reflection.io
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 Reflection.io. All rights reserved.
//
package io.reflection.app.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author billy1380
 * 
 */
public interface Images extends ClientBundle {
    public static final Images INSTANCE = GWT.create(Images.class);

    @Source("logo.png")
    ImageResource logo();

    @Source("reflectionlogobeta.png")
    ImageResource reflectionLogoBeta();

    @Source("spinner.gif")
    ImageResource spinner();

    @Source("spinnerinfo.gif")
    ImageResource spinnerInfo();

    @Source("spinnertableodd.gif")
    ImageResource spinnerTableOdd();

    @Source("spinnertableeven.gif")
    ImageResource spinnerTableEven();

    @Source("alarmclock.png")
    ImageResource alarmClock();

    @Source("clipboard.png")
    ImageResource clipboard();

    @Source("tick.png")
    ImageResource tick();

    @Source("welcomelogo.png")
    ImageResource welcomeLogo();

    @Source("linkaccount.png")
    ImageResource linkAccount();

    @Source("buttonarrowwhite.png")
    ImageResource buttonArrowWhite();

    @Source("buttonlogin.png")
    ImageResource buttonLogin();

    @Source("welcomeprogress.png")
    ImageResource welcomeProgress();

    @Source("linkaccountprogress1.png")
    ImageResource linkAccountProgress1();

    @Source("linkaccountprogress.png")
    ImageResource linkAccountProgress();

    @Source("trowel.png")
    ImageResource trowel();

    @Source("bubble.png")
    ImageResource bubble();

    @Source("cog.png")
    ImageResource cog();

    @Source("clock.png")
    ImageResource clock();

    @Source("questionmark.png")
    ImageResource questionMark();

    @Source("greentick.png")
    ImageResource greenTick();

    @Source("emailsent.png")
    ImageResource emailSent();

    @Source("preloader.gif")
    ImageResource preloader();

}
