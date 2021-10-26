package io.github.danthe1st.eclipse2gdocs.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;

import javax.security.auth.login.CredentialException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import io.github.danthe1st.eclipse2gdocs.gdocs.EclipseCredentialStorage;
import io.github.danthe1st.eclipse2gdocs.util.GeneralUtil;
import io.github.danthe1st.ide2gdocs.gdocs.GoogleDocsUploader;
import io.github.danthe1st.ide2gdocs.gdocs.GoogleDocsUploaderFactory;

public final class EditorHandler implements IPartListener2,IDocumentListener {
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(SwapStateHandler.class);
	private static final ILog LOGGER = Platform.getLog(BUNDLE);

	private static EditorHandler instance;

	private AbstractTextEditor activePart;
	private IWorkbenchWindow window;
	private final GoogleDocsUploader docsUploader;
	
	private int numberOfAdditionalCharactersToDelete=0;
	
	private EditorHandler(ExecutionEvent event) throws ExecutionException, IOException, GeneralSecurityException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		window.getPartService().addPartListener(this);
		docsUploader = setupOAuth2(event);

	}

	public static synchronized EditorHandler getInstance(ExecutionEvent event) throws ExecutionException, IOException, GeneralSecurityException {
		if (instance == null) {
			instance = new EditorHandler(event);
		}
		return instance;
	}

	public void setupGoogleDocument(ExecutionEvent event) throws ExecutionException, IOException {
		String docId = GeneralUtil.promptText(event, "Google Docs Setup", "Please enter the document ID");
		if (docId != null) {
			docsUploader.setDocument(docId);
			enable();
		}
	}

	private static GoogleDocsUploader setupOAuth2(ExecutionEvent event)
			throws ExecutionException, IOException, GeneralSecurityException {
		GoogleDocsUploaderFactory uploaderFactory = new GoogleDocsUploaderFactory(new EclipseCredentialStorage(BUNDLE));
		var uploaderHolder = new Object() {
			private GoogleDocsUploader uploader;
			private IOException ioe;
			private GeneralSecurityException gse;
			private boolean finished;
		};
		Dialog dlg = new MessageDialog(HandlerUtil.getActiveShellChecked(event), "Please wait", null,
				"Please authorize the application in your web browser", 0, 0, "Cancel");
		Thread uploaderCreationThread = new Thread(() -> {
			try {
				uploaderHolder.uploader = uploaderFactory.build();
			} catch (GeneralSecurityException e) {
				uploaderHolder.gse = e;
			} catch (IOException e) {
				uploaderHolder.ioe = e;
			} finally {
				uploaderHolder.finished = true;
				Display.getDefault().asyncExec(dlg::close);
			}
		});
		uploaderCreationThread.start();
		dlg.setBlockOnOpen(true);
		dlg.open();
		if (!uploaderHolder.finished) {
			uploaderFactory.cancel();
			throw new CredentialException("Authorization cancelled");
		} else if (uploaderHolder.uploader != null) {
			return uploaderHolder.uploader;
		} else if (uploaderHolder.ioe != null) {
			throw uploaderHolder.ioe;
		} else if (uploaderHolder.gse != null) {
			throw uploaderHolder.gse;
		} else {
			throw new IllegalStateException("creating google docs uploader failed in an unknown way");
		}
	}

	public void setActivePart(AbstractTextEditor activePart) {
		if(this.activePart!=null) {
			disable();
		}
		this.activePart = activePart;
	}
	
	private void enable() {
		IDocumentProvider provider = activePart.getDocumentProvider();
		IEditorInput input = activePart.getEditorInput();
		IDocument document = provider.getDocument(input);
		document.addDocumentListener(this);
		
		String text = document.get();
		if (docsUploader == null) {
			LOGGER.error("missing connection to a google doc");
		} else {
			docsUploader.overwriteEverything(text, e -> LOGGER.error("Cannot send to Google docs", e));
		}
	}
	private void disable() {
		IDocumentProvider provider = activePart.getDocumentProvider();

		IEditorInput input = activePart.getEditorInput();
		IDocument document = provider.getDocument(input);
		document.removeDocumentListener(this);
	}
	
	public AbstractTextEditor getActivePart() {
		return activePart;
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (activePart == null || !activePart.equals(partRef.getPart(false))) {
			return;
		}
		disable();
		activePart = null;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		numberOfAdditionalCharactersToDelete=0;
		try {
			int startLineNum=event.getDocument().getNumberOfLines(0,event.getOffset())-1;
			int numOfLines=event.getDocument().getNumberOfLines(event.getOffset(), event.getLength())-1;
			numberOfAdditionalCharactersToDelete-=countExcessiveLineBreaks(event.getDocument(),startLineNum,numOfLines);
		} catch (BadLocationException e) {
			LOGGER.warn("Cannot get changes in current document (Document String between offset and length of change not found)", e);
		}
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		try {
			int excessiveLineBreakCharacterCount=countExcessiveLineBreaks(event.getDocument(), 0, event.getDocument().getNumberOfLines(0, event.getOffset())-1);
			docsUploader.overwritePart(event.getText(), event.getOffset()-excessiveLineBreakCharacterCount, event.getLength()+numberOfAdditionalCharactersToDelete, event.getDocument().get(), e -> LOGGER.error("Cannot send to Google docs", e));
		} catch (BadLocationException e1) {
			LOGGER.warn("change offset smaller than document length",e1);
			docsUploader.overwriteEverything(event.getDocument().get(), e -> LOGGER.error("Cannot send to Google docs", e));
		}
		numberOfAdditionalCharactersToDelete=0;
	}
	
	private static int countExcessiveLineBreaks(IDocument doc,int start,int len) throws BadLocationException {
		int ret=0;
		for(int i=start;i<start+len;i++) {
			String delimiter=doc.getLineDelimiter(i);
			if(delimiter!=null&&delimiter.length()>1) {
				ret+=delimiter.length()-1;
			}
		}
		return ret;
	}
}
