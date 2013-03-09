package com.anthony.ctf.nfc.utilities;

import java.nio.charset.Charset;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

public class NFCHelper {
	
    /** Creates a custom MIME type encapsulated in an NDEF record
    *
    * @param mimeType
    */
   public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
       byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
       NdefRecord mimeRecord = new NdefRecord(
               NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
       return mimeRecord;
   }
   
   /**
    * Parses the NDEF Message from the intent and prints to the TextView
    */
   public String processIntent(Intent intent) {
       Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
               NfcAdapter.EXTRA_NDEF_MESSAGES);
       // only one message sent during the beam
       NdefMessage msg = (NdefMessage) rawMsgs[0];
       // record 0 contains the MIME type, record 1 is the AAR, if present
       return new String(msg.getRecords()[0].getPayload());
   }
    
}
