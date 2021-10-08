package io.github.danthe1st.eclipse2gdocs.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.security.auth.login.CredentialException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import io.github.danthe1st.eclipse2gdocs.util.GeneralUtil;

public class SwapStateHandler extends AbstractHandler {

	private static final ILog LOGGER = GeneralUtil.getLogger(SwapStateHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		EditorHandler editorHandler;
		try {
			editorHandler = EditorHandler.getInstance(event);
			IWorkbenchPart activePart = window.getPartService().getActivePart();

			if (activePart instanceof AbstractTextEditor && !activePart.equals(editorHandler.getActivePart())) {
				editorHandler.setActivePart((AbstractTextEditor) activePart);
				editorHandler.setupGoogleDocument(event);
			} else {
				editorHandler.setActivePart(null);
			}

		} catch (GoogleJsonResponseException e) {
			GoogleJsonError details = e.getDetails();
			if (details != null) {
				String message = details.getMessage();
				if (message != null) {
					GeneralUtil.showError(event, "Google communication failed", message);
					return null;
				}
			}
			GeneralUtil.showError(event,
					"An error occured while communicating with the google API. See the log for details.",e.getMessage());
			LOGGER.error("Communicating with the Google API failed", e);
		} catch (CredentialException e) {
			GeneralUtil.showError(event, "OAuth2 error", "Invalid credentials", e.getMessage());
		} catch (GeneralSecurityException e) {
			GeneralUtil.showError(event,
					"An error occured while communicating with the google API. See the log for details.",e.getMessage());
			LOGGER.error("Communicating with the Google API failed", e);
		}  catch (IOException e) {
			GeneralUtil.showError(event,
					"An I/O error occured trying to set up a connecion with Google Docs. See the log for details.",e.getMessage());
			LOGGER.error("I/O error during Google Docs setup", e);
		}

		return null;
	}

}
