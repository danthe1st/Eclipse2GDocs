package io.github.danthe1st.eclipse2gdocs.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class Logger {
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(Logger.class);
	private static final ILog LOGGER = Platform.getLog(BUNDLE);
	
	private Logger() {
		// prevent instantialtion
	}
	
	public static void error(String msg) {
		log(IStatus.ERROR, msg);
	}
	
	public static void error(String msg, Throwable e) {
		log(IStatus.ERROR, msg, e);
	}
	
	public static void warn(String msg) {
		log(IStatus.WARNING, msg);
	}
	
	public static void warn(String msg, Throwable e) {
		log(IStatus.WARNING, msg, e);
	}
	
	private static void log(int level, String msg) {
		LOGGER.log(new Status(level, "io.github.danthe1st.eclipse2gdocs", msg));
	}
	
	private static void log(int level, String msg, Throwable e) {
		LOGGER.log(new Status(level, "io.github.danthe1st.eclipse2gdocs", msg, e));
	}
}
