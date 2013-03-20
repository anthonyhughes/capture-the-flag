package com.anthony.ctf.game;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.anthony.ctf.R;
import com.anthony.ctf.bluetooth.EnemyDetectionActivity;
import com.anthony.ctf.maps.MapsActivity;
import com.anthony.ctf.nfc.CommunicationActivity;
import com.anthony.ctf.utilities.AndroidHelper;
import com.anthony.ctf.utilities.StringHelper;
import com.anthony.ctf.utilities.WebServiceConnector;
import com.anthony.ctf.utilities.XmlDocumentHelper;

public class MainActivity extends Activity {

	// UI Elements
	Button nfcButton;
	Button bluetoothButton;
	Button mapsButton;
	Button checkStatus;
	BluetoothAdapter bluetoothAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Work around for HTTP calls
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.ctf);
		initialiseBluetoothAvilability();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		nfcButton = (Button) findViewById(R.id.button1);
		findViewById(R.id.button1).setOnClickListener(commsButtonListener);
		bluetoothButton = (Button) findViewById(R.id.button2);
		findViewById(R.id.button2).setOnClickListener(bluetoothButtonListener);
		mapsButton = (Button) findViewById(R.id.button3);
		findViewById(R.id.button3).setOnClickListener(mapsButtonListener);
		checkStatus = (Button) findViewById(R.id.button4);
		findViewById(R.id.button4).setOnClickListener(checkListener);

	}

	private void initialiseBluetoothAvilability() {
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		startActivity(discoverableIntent);
	}

	private View.OnClickListener commsButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent nfc = new Intent(MainActivity.this,
					CommunicationActivity.class);
			startActivity(nfc);
		}
	};

	private View.OnClickListener bluetoothButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent bluetooth = new Intent(MainActivity.this,
					EnemyDetectionActivity.class);
			startActivity(bluetooth);
		}
	};

	private View.OnClickListener mapsButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent maps = new Intent(MainActivity.this, MapsActivity.class);
			startActivity(maps);
		}
	};

	private View.OnClickListener checkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				String bluetoothName = bluetoothAdapter.getName();
				String addy = bluetoothAdapter.getAddress();
				String document = WebServiceConnector.fetchGameDocument();
				LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				if (document.contains(bluetoothName)) {
					// Device is already registered
					AndroidHelper.toast(MainActivity.this, "Your device "
							+ bluetoothName
							+ " is registered. Hunt for the flag!");
				} else {
					// Register device
					Document update = StringHelper.stringToXmlDoc(document);
					// Update parsed document
					update.getChildNodes()
							.item(0)
							.appendChild(
									XmlDocumentHelper.buildPlayerNode(update,
											bluetoothName, addy, latitude,
											longitude));

					// Push to web service
					WebServiceConnector.updateGameDocument(update);
					AndroidHelper.toast(MainActivity.this, "Your device "
							+ bluetoothName
							+ " is registered. Hunt for the flag!");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

}
