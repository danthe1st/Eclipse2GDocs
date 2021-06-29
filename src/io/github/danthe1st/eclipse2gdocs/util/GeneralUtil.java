package io.github.danthe1st.eclipse2gdocs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;

import io.github.danthe1st.eclipse2gdocs.gdocs.GoogleDocsUploader;

public final class GeneralUtil {
	
	private static final String GOOGLE_OAUTH_CREDENTIALS_FILE="oauth_credentials.txt";
	
	private static final Bundle bundle=FrameworkUtil.getBundle(GeneralUtil.class);
	
	private GeneralUtil(){
		//utility class, prevent instantiation
	}
	
	public static ILog getLogger(Class<?> cl) {
		Bundle bundle = FrameworkUtil.getBundle(cl);
		return Platform.getLog(bundle);
	}
	
	public static void saveGoogleOAuthCredentials(Details credentialDetails) throws IOException {
		File file = getStorageDir();
		try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file,GOOGLE_OAUTH_CREDENTIALS_FILE)),StandardCharsets.UTF_8))){
			bw.write(credentialDetails.getClientId());
			bw.newLine();
			bw.write(credentialDetails.getClientSecret());
		}
	}
	
	public static Details loadGoogleOAuthCredentials() throws IOException {
		File file=new File(getStorageDir(),GOOGLE_OAUTH_CREDENTIALS_FILE);
		if(!file.exists()) {
			return null;
		}
		 try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8))){
			 String clientId=br.readLine();
			 String clientSecret=br.readLine();
			 return GoogleDocsUploader.getCredentialDetails(clientId, clientSecret);
		 }
	}
	
	private static File getStorageDir() {
		IPath stateLocation = Platform.getStateLocation(bundle);
		return stateLocation.toFile();
	}
	public static String promptText(ExecutionEvent event,String title,String promptText) throws ExecutionException {
		InputDialog dlg = new InputDialog(HandlerUtil.getActiveShellChecked(event), title, promptText,
				"", null);
		if (dlg.open() == Window.OK) {
			return dlg.getValue();
		}
		return null;
	}
	public static void showError(ExecutionEvent event,String msg,String reason) throws ExecutionException {
		showError(event, "An error occured",msg,reason);
	}
	public static void showError(ExecutionEvent event,String title,String msg,String reason) throws ExecutionException {
		ErrorDialog.openError(HandlerUtil.getActiveShellChecked(event), title, msg, Status.error(reason));
	}
}
