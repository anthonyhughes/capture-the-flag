package com.anthony.ctf.utilities;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

public class WebServiceConnector {
	
	private static final String WEB_SERVICE_URL = "http://192.168.0.8:8012/v1/documents?uri=/game";
	private static final String IP = "192.168.0.8";
	
	public WebServiceConnector() {}
	
	public static String fetchGameDocument() throws ClientProtocolException, IOException{		
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(
					new AuthScope(IP, 8012),
					new UsernamePasswordCredentials("admin","admin"));
		HttpGet httpGet = new HttpGet(WEB_SERVICE_URL);
		HttpResponse httpResponse = client.execute(httpGet);
		HttpEntity resEntity = httpResponse.getEntity();
		return EntityUtils.toString(resEntity);
	}

	public static void updateGameDocument(Document doc) throws ClientProtocolException, IOException {	
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(
					new AuthScope(IP, 8012),
					new UsernamePasswordCredentials("admin","admin"));
		HttpPut httpPut = new HttpPut(WEB_SERVICE_URL);
		httpPut.setEntity(new StringEntity(StringHelper.docToString(doc)));
		httpPut.setHeader("Content-Type", "application/xml");
		client.execute(httpPut);
	}
	
	public static String retrieveNameOfFlagHolder() throws ClientProtocolException, IOException {
		Document game = StringHelper.stringToXmlDoc(fetchGameDocument());
		return game.getChildNodes().item(0).getChildNodes().item(0).getTextContent();
	}
	
}
