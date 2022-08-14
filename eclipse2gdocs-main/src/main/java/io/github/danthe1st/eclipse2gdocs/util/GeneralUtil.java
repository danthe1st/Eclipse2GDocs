package io.github.danthe1st.eclipse2gdocs.util;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public final class GeneralUtil {
	
	private static final Bundle bundle = FrameworkUtil.getBundle(GeneralUtil.class);
	
	private GeneralUtil() {
		// utility class, prevent instantiation
	}
	
	public static ILog getLogger(Class<?> cl) {
		Bundle bundle = FrameworkUtil.getBundle(cl);
		return Platform.getLog(bundle);
	}
	
	public static File getCredentialFile() {
		return new File(getStorageDir(), "tokens");
	}
	
	private static File getStorageDir() {
		IPath stateLocation = Platform.getStateLocation(bundle);
		return stateLocation.toFile();
	}
	
	public static String promptText(ExecutionEvent event, String title, String promptText) throws ExecutionException {
		InputDialog dlg = new InputDialog(
				HandlerUtil.getActiveShellChecked(event), title, promptText,
				"", null
		);
		if(dlg.open() == Window.OK){
			return dlg.getValue();
		}
		return null;
	}
	
	public static void showError(ExecutionEvent event, String msg, String reason) throws ExecutionException {
		showError(event, "An error occured", msg, reason);
	}
	
	public static void showError(ExecutionEvent event, String title, String msg, String reason) throws ExecutionException {
		ErrorDialog.openError(HandlerUtil.getActiveShellChecked(event), title, msg, new Status(IStatus.ERROR, "io.github.danthe1st.eclipse2gdocs", reason));
	}
	
}
