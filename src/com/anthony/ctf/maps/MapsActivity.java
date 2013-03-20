package com.anthony.ctf.maps;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

import com.anthony.ctf.R;
import com.anthony.ctf.utilities.StringHelper;
import com.anthony.ctf.utilities.WebServiceConnector;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapController;

public class MapsActivity extends Activity {

	private GoogleMap map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Override Network Policy
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.frag))
				.getMap();
		addEnemyMarkersToMap();
	}

	private void addEnemyMarkersToMap() {
		// Fetch Game Document
		try {
			Document document = StringHelper.stringToXmlDoc(WebServiceConnector
					.fetchGameDocument());
			NodeList players = document.getElementsByTagName("player");
			for (int i = 0; i < players.getLength(); i++) {
				Node node = players.item(i);
				LatLng enemyLatLng = new LatLng(Double.parseDouble(node
						.getChildNodes().item(2).getChildNodes().item(0)
						.getTextContent()), Double.parseDouble(node
						.getChildNodes().item(2).getChildNodes().item(1)
						.getTextContent()));
				map.addMarker(new MarkerOptions()
						.position(enemyLatLng).title(node.getChildNodes().item(0).getTextContent())
						.snippet("Enemy gonna steal your flag!!"));
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		}
	}
}
