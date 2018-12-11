package org.alexdev.kepler.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

	public static Logger getErrorLogger() {
		return LoggerFactory.getLogger("ErrorLogger");
	}
}
