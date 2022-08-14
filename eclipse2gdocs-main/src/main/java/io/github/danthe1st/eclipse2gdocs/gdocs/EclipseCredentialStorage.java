package io.github.danthe1st.eclipse2gdocs.gdocs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.osgi.framework.Bundle;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.Builder;

import io.github.danthe1st.ide2gdocs.gdocs.CredentialStorage;

public class EclipseCredentialStorage implements CredentialStorage{

	private File dataFile;
	
	public EclipseCredentialStorage(Bundle bundle) {
		dataFile=bundle.getDataFile("oauth-credential.dat");
	}

	@Override
	public Credential loadCredential(Builder credBuilder) throws IOException {
		if(!dataFile.exists()) {
			return null;
		}
		try(DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(dataFile)))){
			credBuilder.setTokenServerEncodedUrl(dis.readUTF());
			Credential ret=credBuilder.build();
			ret.setAccessToken(dis.readUTF());
			ret.setRefreshToken(dis.readUTF());
			long expTime=dis.readLong();
			ret.setExpirationTimeMilliseconds(expTime==-1?null:expTime);
			return ret;
		}
	}

	@Override
	public void saveCredential(Credential cred) throws IOException {
		try(DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)))){
			dos.writeUTF(cred.getTokenServerEncodedUrl());
			dos.writeUTF(cred.getAccessToken());
			dos.writeUTF(cred.getRefreshToken());
			Long expTimeMS=cred.getExpirationTimeMilliseconds();
			if(expTimeMS==null) {
				expTimeMS=-1L;
			}
			dos.writeLong(expTimeMS);
		}
	}
	
}
