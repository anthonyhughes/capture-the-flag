package com.anthony.ctf.nfc.utilities;

import java.io.IOException;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import com.anthony.ctf.utilities.AndroidHelper;

public class NFCHelper {

	public NFCHelper() {
	}

	public static boolean writeTag(Context context, NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					AndroidHelper.toast(context, "Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					AndroidHelper.toast(context,
							"Tag capacity is " + ndef.getMaxSize()
									+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				AndroidHelper.toast(context,
						"Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						AndroidHelper.toast(context,
								"Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						AndroidHelper.toast(context, "Failed to format tag.");
						return false;
					}
				} else {
					AndroidHelper.toast(context, "Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			AndroidHelper.toast(context, "Failed to write tag");
		}

		return false;
	}
	
	public static NdefMessage getStringAsNdef(String message) {
		byte[] textBytes = message.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

}
