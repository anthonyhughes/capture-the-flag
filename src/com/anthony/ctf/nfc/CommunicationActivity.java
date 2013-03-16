package com.anthony.ctf.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anthony.ctf.R;

public class CommunicationActivity extends Activity {
	
	Button nfcButton;
	Button messageButton;
	
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.communication);
		
		nfcButton = (Button) findViewById(R.id.ptf); 
		findViewById(R.id.ptf).setOnClickListener(nfcClick);
		messageButton = (Button) findViewById(R.id.pam);
		findViewById(R.id.pam).setOnClickListener(messageClick);
	}
	
	private View.OnClickListener nfcClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent nfc = new Intent(CommunicationActivity.this, PassFlagActivity.class);
            startActivity(nfc);
        }
    };
    
    private View.OnClickListener messageClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
        	Intent bluetooth = new Intent(CommunicationActivity.this, NFCActivity.class);
        	startActivity(bluetooth);
        }
    };

}
