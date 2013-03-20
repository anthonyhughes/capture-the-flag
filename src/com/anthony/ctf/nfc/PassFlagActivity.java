package com.anthony.ctf.nfc;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.TextView;

import com.anthony.ctf.R;
import com.anthony.ctf.nfc.utilities.NFCHelper;
import com.anthony.ctf.utilities.AndroidHelper;
import com.anthony.ctf.utilities.WebServiceConnector;

public class PassFlagActivity extends Activity {

	TextView flagIndicator;

	NfcAdapter nfcAdapter;
	public boolean resumed = false;
	public boolean writeMode = false;
	public boolean flagHolder = false;

	PendingIntent nfcPendingIntent;
	IntentFilter[] writeTagFilters;
	IntentFilter[] ndefExchangeFilters;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		setContentView(R.layout.pass_flag);
		flagIndicator = (TextView) findViewById(R.id.indicator);

		if (resumed) {
			nfcAdapter.enableForegroundNdefPush(PassFlagActivity.this,
					flagAsNdef());
		}

		// Handle all of our received NFC intents in this activity.
		nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		ndefExchangeFilters = new IntentFilter[] { ndefDetected };

		// Only the flag holder can write to a tag
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		writeTagFilters = new IntentFilter[] { tagDetected };
	}

	private boolean isFlagHolder() {
		// Get flag information
		String currentFlagHolder = "";
		try {
			currentFlagHolder = WebServiceConnector.retrieveNameOfFlagHolder();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		String deviceName = bluetoothAdapter.getName();
		if (deviceName.equals(currentFlagHolder)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		resumed = true;
		// Sticky notes received from Android
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			NdefMessage[] messages = getNdefMessages(getIntent());
			byte[] payload = messages[0].getRecords()[0].getPayload();
			persistFlagToWebService(new String(payload));
			setIntent(new Intent());
		}
		enableNdefExchangeMode();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		resumed = false;
		nfcAdapter.disableForegroundNdefPush(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (!writeMode
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = getNdefMessages(intent);
			dialogConfirm(msgs[0]);
		}

		// Tag writing mode
		if (writeMode
				&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NFCHelper.writeTag(this, flagAsNdef(), detectedTag);
		}
	}

	/**
	 * Refactor and store
	 * 
	 * @param msg
	 */
	private void dialogConfirm(final NdefMessage msg) {
		new AlertDialog.Builder(this)
				.setTitle("You have received the flag!")
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								String body = new String(msg.getRecords()[0]
										.getPayload());
								persistFlagToWebService(body);
							}
						}).show();
	}

	private void persistFlagToWebService(String body) {
		if(body.equals("Flag")){
			AndroidHelper.toast(this, body);
		} else {
			AndroidHelper.toast(this, "This user did not have the flag!");			
		}
	}

	private NdefMessage flagAsNdef() {
		if(isFlagHolder()){
			return NFCHelper.getStringAsNdef("Flag");
		} else {
			return NFCHelper.getStringAsNdef("No Flag");
		}
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			finish();
		}
		return msgs;
	}

	@SuppressWarnings("deprecation")
	private void enableNdefExchangeMode() {
		nfcAdapter
				.enableForegroundNdefPush(PassFlagActivity.this, flagAsNdef());
		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent,
				ndefExchangeFilters, null);
	}
}
