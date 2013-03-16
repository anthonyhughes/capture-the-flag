package com.anthony.ctf.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anthony.ctf.R;
import com.anthony.ctf.bluetooth.DeviceListActivity;
import com.anthony.ctf.maps.MapsActivity;
import com.anthony.ctf.nfc.CommunicationActivity;
import com.anthony.ctf.nfc.NFCActivity;

public class MainActivity extends Activity {
	
	//UI Elements
	Button nfcButton;
	Button bluetoothButton;
	Button mapsButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ctf);
		initialise();
		
		nfcButton = (Button) findViewById(R.id.button1);
		findViewById(R.id.button1).setOnClickListener(commsButtonListener);
		bluetoothButton = (Button) findViewById(R.id.button2); 
		findViewById(R.id.button2).setOnClickListener(bluetoothButtonListener);
		mapsButton = (Button) findViewById(R.id.button3);
		findViewById(R.id.button3).setOnClickListener(mapsButtonListener);
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
	
}
