package com.anthony.ctf.game;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.anthony.ctf.R;
import com.anthony.ctf.bluetooth.DeviceListActivity;
import com.anthony.ctf.maps.MapsActivity;
import com.anthony.ctf.nfc.CommunicationActivity;
import com.anthony.ctf.utilities.WebServiceConnector;

public class MainActivity extends Activity {
	
	//UI Elements
	Button nfcButton;
	Button bluetoothButton;
	Button mapsButton;
	Button checkStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Work around for HTTP calls
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.ctf);
		initialise();
		
		nfcButton = (Button) findViewById(R.id.button1);
		findViewById(R.id.button1).setOnClickListener(commsButtonListener);
		bluetoothButton = (Button) findViewById(R.id.button2); 
		findViewById(R.id.button2).setOnClickListener(bluetoothButtonListener);
		mapsButton = (Button) findViewById(R.id.button3);
		findViewById(R.id.button3).setOnClickListener(mapsButtonListener);
		checkStatus = (Button) findViewById(R.id.button4);
		findViewById(R.id.button4).setOnClickListener(checkListener);
		
	}
	
    private void initialise() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		startActivity(discoverableIntent);
	}

	private View.OnClickListener commsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent nfc = new Intent(MainActivity.this, CommunicationActivity.class);
            startActivity(nfc);
        }
    };
    
    private View.OnClickListener bluetoothButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
        	Intent bluetooth = new Intent(MainActivity.this, DeviceListActivity.class);
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
				String response = WebServiceConnector.fetchGameDocument();
				Log.d("HTTP - ", response);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	};
	
}
