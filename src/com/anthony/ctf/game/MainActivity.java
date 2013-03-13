package com.anthony.ctf.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anthony.ctf.R;
import com.anthony.ctf.nfc.NFCActivity;
import com.anthony.ctf.utilities.AndroidHelper;

public class MainActivity extends Activity {
	
	Button nfcButton;
	Button bluetoothButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ctf);
		
		nfcButton = (Button) findViewById(R.id.button1);
		findViewById(R.id.button1).setOnClickListener(nfcButtonListener);
		bluetoothButton = (Button) findViewById(R.id.button2);
		findViewById(R.id.button2).setOnClickListener(bluetoothButtonListener);
	}
	
    private View.OnClickListener nfcButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent nfc = new Intent(MainActivity.this, NFCActivity.class);
            startActivity(nfc);
        }
    };
    
    private View.OnClickListener bluetoothButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
        	AndroidHelper.toast(MainActivity.this, "Hello2!");
        }
    };
	
	
	
}
