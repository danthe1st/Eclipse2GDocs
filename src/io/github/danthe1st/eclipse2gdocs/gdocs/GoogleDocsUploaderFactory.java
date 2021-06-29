package io.github.danthe1st.eclipse2gdocs.gdocs;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;

public class GoogleDocsUploaderFactory {

	private static final String APPLICATION_NAME = "GoogleDocsUploader";
	private static final List<String> SCOPES = Collections.singletonList(DocsScopes.DOCUMENTS);
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private LocalServerReceiver receiver;
	private GoogleClientSecrets clientSecrets;

	public GoogleDocsUploaderFactory(Details credentialDetails) {
		clientSecrets = new GoogleClientSecrets();
		clientSecrets.setInstalled(credentialDetails);
		receiver = new LocalServerReceiver.Builder().setPort(8888).build();
	}

	public GoogleDocsUploader build() throws IOException, GeneralSecurityException {
		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, SCOPES).setAccessType("offline").build();
		Docs service = new Docs.Builder(httpTransport, JSON_FACTORY,
				new AuthorizationCodeInstalledApp(flow, receiver).authorize("user"))
						.setApplicationName(APPLICATION_NAME).build();
		return new GoogleDocsUploader(service);
	}

	public void cancel() throws IOException {
		receiver.stop();
	}
}
