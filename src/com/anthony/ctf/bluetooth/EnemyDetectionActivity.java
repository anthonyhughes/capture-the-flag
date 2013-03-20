package com.anthony.ctf.bluetooth;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.anthony.ctf.R;
import com.anthony.ctf.utilities.AndroidHelper;
import com.anthony.ctf.utilities.StringHelper;
import com.anthony.ctf.utilities.WebServiceConnector;

public class EnemyDetectionActivity extends Activity {
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> newDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bluetooth);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        newDevices = new ArrayAdapter<String>(this, R.layout.device);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(newDevices);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
        	bluetoothAdapter.enable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        if(bluetoothAdapter.isEnabled()){
        	bluetoothAdapter.disable();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    // Interact with list devices
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            bluetoothAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String[] data = info.split("\n");
            int proximity = Integer.parseInt(data[2].split(" ")[2]);
            String onClickName = data[0].split(" ")[2]; 
            String currentFlagHolder = "";
            try {
				currentFlagHolder = WebServiceConnector.retrieveNameOfFlagHolder();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            //If use is close to enemy then continue
            if(Math.abs(proximity) < 50){
            	//if the clicked user has flag then raise dialog
            	if(onClickName.equals(currentFlagHolder)){
	                new AlertDialog.Builder(EnemyDetectionActivity.this).setTitle("Flag Located. Take the flag?!!")
	                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface arg0, int arg1) {
	                    	//Update
	        				try {
								String document = WebServiceConnector.fetchGameDocument();
								Document update = StringHelper.stringToXmlDoc(document);
								update.getChildNodes().item(0).getChildNodes().item(0).setTextContent(bluetoothAdapter.getName());
								WebServiceConnector.updateGameDocument(update);
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
	                    	AndroidHelper.toast(EnemyDetectionActivity.this, "You now have the flag. RUN!");
	                    }
	                })
	                .setNegativeButton("No!", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface arg0, int arg1) {
	                    	//
	                    }
	                })
	                .show();
            	} else {
            		AndroidHelper.toast(EnemyDetectionActivity.this, "This enemy does not possess the flag.");
            	}
                //else do nothing
            } else {
            	AndroidHelper.toast(EnemyDetectionActivity.this, "Get closer to your enemy!");
            }
        }
    };

    // The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            short strength = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevices.add("Name : " + device.getName() + "\nAddress : " + device.getAddress() + "\nProximity : " + strength);
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (newDevices.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDevices.add(noDevices);
                }
            }
        }
    };

}
