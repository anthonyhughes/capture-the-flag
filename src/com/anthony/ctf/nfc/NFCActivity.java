/*
 * Copyright 2011, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anthony.ctf.nfc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anthony.ctf.R;
import com.anthony.ctf.nfc.utilities.NFCHelper;
import com.anthony.ctf.utilities.FileHelper;

public class NFCActivity extends Activity {
	
    NfcAdapter nfcAdapter;
    EditText editedText;
    Button button;
    
    public boolean resumed = false;
    public boolean writeMode = false;
    PendingIntent nfcPendingIntent;
    IntentFilter[] writeTagFilters;
    IntentFilter[] ndefExchangeFilters;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        setContentView(R.layout.nfc);

        editedText = ((EditText) findViewById(R.id.note));
        editedText.addTextChangedListener(mTextWatcher);
        button = (Button) findViewById(R.id.viewMessages);
        button.setOnClickListener(messagesButton);
        
        
        // Handle all of our received NFC intents in this activity.
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (MalformedMimeTypeException e) { }
        ndefExchangeFilters = new IntentFilter[] { ndefDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            saveMessageToFile(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }
        enableNdefExchangeMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumed = false;
        nfcAdapter.disableForegroundNdefPush(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!writeMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            propmptUser(msgs[0]);
        }

        // Tag writing mode
        if (writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NFCHelper.writeTag(this, getNoteAsNdef(), detectedTag);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        @Override
        public void afterTextChanged(Editable arg0) {
            if (resumed) {
                nfcAdapter.enableForegroundNdefPush(NFCActivity.this, getNoteAsNdef());
            }
        }
    };

    private View.OnClickListener messagesButton = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
        	Intent intent = new Intent(NFCActivity.this, MessagesListActivity.class);
        	startActivity(intent);
        }
    };

    /**
     * Refactor and store
     * @param msg
     */
    private void propmptUser(final NdefMessage msg) {
        new AlertDialog.Builder(this).setTitle("Message Saved!")
            .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    String body = new String(msg.getRecords()[0].getPayload());
                    saveMessageToFile(body);
                }
            }).show();
    }

    private void saveMessageToFile(String body) {
    	FileOutputStream outputStream;
		try {
			outputStream = openFileOutput(FileHelper.FILENAME, Context.MODE_APPEND);
			String tmp = body + ",";
	    	outputStream.write(tmp.getBytes());
	    	outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }

    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = editedText.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        return new NdefMessage(new NdefRecord[] {
            textRecord
        });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                    record
                });
                msgs = new NdefMessage[] {
                    msg
                };
            }
        } else {
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        nfcAdapter.enableForegroundNdefPush(NFCActivity.this, getNoteAsNdef());
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, ndefExchangeFilters, null);
    }
}