package com.spacehopperstudios.storedatacollector.logging;

import java.util.logging.Level;

public final class GaeLevel extends Level {

	private static final long serialVersionUID = 1L;

	protected GaeLevel(String name, int value) {
		super(name, value);
	}

	public static final Level DEBUG = new GaeLevel("DEBUG", 800);

	public static final Level TRACE = new GaeLevel("TRACE", 700);

}
