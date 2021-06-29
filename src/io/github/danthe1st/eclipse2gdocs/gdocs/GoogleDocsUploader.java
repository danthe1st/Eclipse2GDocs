package io.github.danthe1st.eclipse2gdocs.gdocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.eclipse.core.runtime.ILog;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentResponse;
import com.google.api.services.docs.v1.model.DeleteContentRangeRequest;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.InsertTextRequest;
import com.google.api.services.docs.v1.model.Location;
import com.google.api.services.docs.v1.model.Range;
import com.google.api.services.docs.v1.model.Request;
import com.google.api.services.docs.v1.model.Response;
import com.google.api.services.docs.v1.model.StructuralElement;

import io.github.danthe1st.eclipse2gdocs.util.GeneralUtil;

public class GoogleDocsUploader {

	private static final ILog LOGGER = GeneralUtil.getLogger(GoogleDocsUploader.class);
	private ExecutorService threadPool=Executors.newSingleThreadExecutor();
	private final Docs service;
	private Document doc;
	private int startIndex;
	private int lastLen;
	private int lastHashCode=-1;

	public GoogleDocsUploader(Docs service){
		this.service=service;
	}
	
	public static Details getCredentialDetails(String clientId, String clientSecret) {
		Details details = new Details();
		details.setClientId(clientId);
		details.setClientSecret(clientSecret);
		return details;
	}

	public synchronized void setDocument(String id) throws IOException {
		doc = service.documents().get(id).execute();
		startIndex=getLastIndex()-1;
		lastLen=0;
		lastHashCode=-1;
		LOGGER.info("set up google document: " + doc.getTitle());
	}

	public void overwriteEverything(String newText,Consumer<IOException> exceptionHandler) {
		threadPool.execute(()->{
			try {
				actuallyOverwriteEverything(newText);
			} catch (IOException e) {
				exceptionHandler.accept(e);
			}
		});
	}
	
	private synchronized void actuallyOverwriteEverything(String newText) throws IOException {
		try {
			int hashCode=newText.hashCode();
			if(lastHashCode==hashCode) {
				return;
			}
			int firstIndex=startIndex;
			int lastIndex=startIndex+lastLen-1;
			List<Request> req=new ArrayList<>();
			if(lastIndex>firstIndex) {
				req.add(new Request().setDeleteContentRange(
							new DeleteContentRangeRequest().setRange(new Range().setStartIndex(firstIndex).setEndIndex(lastIndex))));
			}
			if(!newText.isEmpty()) {
				req.add(new Request().setInsertText(new InsertTextRequest().setText(newText).setLocation(new Location().setIndex(firstIndex))));
			}
			if(!req.isEmpty()) {
				executeMultiple(req);
				lastLen=countLengthWithoutWindowsLineBreaks(newText)+1;
				lastHashCode=hashCode;
			}
		}catch(IOException e) {
			doc = service.documents().get(doc.getDocumentId()).execute();
			startIndex=Math.min(startIndex, getLastIndex());
			lastLen=getLastIndex()-startIndex;
			lastHashCode=-1;
			throw e;
		}
	}
	private static int countLengthWithoutWindowsLineBreaks(String toCount) {
		char[] chars = toCount.toCharArray();
		char lastChar='\0';
		int ret=0;
		for (int i = 0; i < chars.length; i++) {
			if(lastChar!='\r'||chars[i]!='\n') {
				ret++;
			}
			lastChar=chars[i];
		}
		return ret;
	}
	private int getLastIndex() {
		List<StructuralElement> content = doc.getBody().getContent();
		return content.get(content.size()-1).getEndIndex();
	}

	private List<Response> executeMultiple(List<Request> requests) throws IOException {
		BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
		BatchUpdateDocumentResponse response = service.documents().batchUpdate(doc.getDocumentId(), body).execute();
		return response.getReplies();
	}
}
