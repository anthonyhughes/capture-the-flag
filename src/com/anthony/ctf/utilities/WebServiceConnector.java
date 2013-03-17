package com.anthony.ctf.utilities;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WebServiceConnector {
	
	private static final String WEB_SERVICE_URL = "http://192.168.0.8:8012/v1/documents?uri=/game";
	
	public WebServiceConnector() {}
	
	public static String fetchGameDocument() throws ClientProtocolException, IOException{		
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(
					new AuthScope("192.168.0.8", 8012),
					new UsernamePasswordCredentials("admin","admin"));
		HttpGet httpGet = new HttpGet(WEB_SERVICE_URL);
		HttpResponse httpResponse = client.execute(httpGet);
		HttpEntity resEntity = httpResponse.getEntity();
		return EntityUtils.toString(resEntity);
	}
}
