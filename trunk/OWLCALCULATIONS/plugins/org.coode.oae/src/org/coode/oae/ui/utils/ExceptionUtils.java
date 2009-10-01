package org.coode.oae.ui.utils;

public class ExceptionUtils {
	public static void checkNullArg(Object o) {
		if (o == null) {
			throw new NullPointerException("Argument cannot be null");
		}
	}
}
