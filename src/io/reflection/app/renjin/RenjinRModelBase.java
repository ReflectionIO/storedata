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
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author billy1380
 * 
 */
public abstract class RenjinRModelBase {

	private static final Logger LOGGER = Logger.getLogger(RenjinRModelBase.class.getName());

	protected ScriptEngine mEngine;
	protected Invocable mInvocableEngine;

	/**
	 * 
	 */
	public RenjinRModelBase() {
		ScriptEngineManager factory = new ScriptEngineManager();

		mEngine = factory.getEngineByName("Renjin");
		mInvocableEngine = (Invocable) mEngine;
	}

	protected void changeWd(String to) throws NoSuchMethodException, ScriptException {
		String wd = mInvocableEngine.invokeFunction("getwd").toString();

		LOGGER.info(wd);

		String newWd = wd.replace("\"", "") + to;

		mInvocableEngine.invokeFunction("setwd", newWd);

		LOGGER.info(mInvocableEngine.invokeFunction("getwd").toString());
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
