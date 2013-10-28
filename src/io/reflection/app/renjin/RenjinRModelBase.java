//
//  RenjinRModelBase.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.renjin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.renjin.appengine.AppEngineContextFactory;

import com.willshex.service.ContextAwareServlet;

/**
 * @author billy1380
 * 
 */
public abstract class RenjinRModelBase {

	private static final Logger LOG = Logger.getLogger(RenjinRModelBase.class.getName());

	protected static final ThreadLocal<ScriptEngine> ENGINE = new ThreadLocal<ScriptEngine>();
	protected ScriptEngine mEngine;
	protected Invocable mInvocableEngine;

	/**
	 * 
	 */
	public RenjinRModelBase() {
		mEngine = ENGINE.get();

		if (mEngine == null) {
			mEngine = AppEngineContextFactory.createScriptEngine(ContextAwareServlet.CONTEXT.get());

			if (mEngine == null) throw new RuntimeException("Could not create an instance of Renjin script engine");
		}

		// mEngine = factory.getEngineByName("Renjin");
		mInvocableEngine = (Invocable) mEngine;
	}

	protected void changeWd(String to) throws NoSuchMethodException, ScriptException {
		String wd = mInvocableEngine.invokeFunction("getwd").toString();

		LOG.info(wd);

		String newWd = wd.replace("\"", "") + to;

		mInvocableEngine.invokeFunction("setwd", newWd);

		LOG.info(mInvocableEngine.invokeFunction("getwd").toString());
	}

	protected void put(String name, Object value) {
		mEngine.put(name, value);
	}

	protected Object get(String name) {
		return mEngine.get(name);
	}

	protected void run(InputStream in) throws ScriptException {
		InputStreamReader reader = new InputStreamReader(in);
		try {
			mEngine.eval(reader);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
