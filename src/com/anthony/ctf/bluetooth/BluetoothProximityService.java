package com.anthony.ctf.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;

public class BluetoothProximityService {
	
    private final BluetoothAdapter adapter;
    private final Handler handler;
    
    public BluetoothProximityService(Context context, Handler handler) {
		// TODO Auto-generated constructor stub
    	adapter = BluetoothAdapter.getDefaultAdapter();
    	this.handler = handler;
	}

	public void stop() {
	}

}
