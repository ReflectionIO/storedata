package com.spacehopperstudios.storedatacollector.datatypes;

public enum StoreType {
	Ios, Android, Amazon;

	public String toString() {
		switch (this) {
		case Ios:
			return "ios";
		case Amazon:
			return "amazon";
		case Android:
			return "android";
		}

		return null;
	}
}
