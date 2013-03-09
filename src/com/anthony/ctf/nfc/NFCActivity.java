package com.anthony.ctf.nfc;

import com.anthony.ctf.nfc.utilities.NFCHelper;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Time;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class NFCActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback{

	NfcAdapter nfcAdapter;
	NFCHelper nfcHelper;
	TextView textView;
	private static final int MESSAGE_SENT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		textView = (TextView) findViewById(R.id.textView1);
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcHelper = new NFCHelper();
		
		if(nfcAdapter == null) {
			textView = (TextView) findViewById(R.id.textView1);
			textView.setText("NFC Unavailable on this device");
		}
		
		nfcAdapter.setNdefPushMessageCallback(this, this);		
		
		nfcAdapter.setOnNdefPushCompleteCallback(this, this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			nfcHelper.processIntent(getIntent());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_nfc, menu);
		return true;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		handler.obtainMessage(MESSAGE_SENT).sendToTarget();
		
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Time time = new Time();
		time.setToNow();
		String text = ("Beam me up! \n\n" + "Beam Time: " + time.format("%H:%M:%S"));
		NdefMessage msg = new NdefMessage(
				new NdefRecord[]{
						nfcHelper.createMimeRecord("application/text", text.getBytes())
				});
		return msg;
	}
	
    /** This handler receives a message from onNdefPushComplete */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };

}
