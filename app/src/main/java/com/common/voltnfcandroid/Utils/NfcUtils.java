package com.common.voltnfcandroid.Utils;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.Locale;

public class NfcUtils {
    private final static  String TAG = "NfcUtils";
    // Set NDEF message
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = 0;
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    // Send NDEF Message
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                return true;
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable != null) {
                    ndefFormatable.connect();
                    ndefFormatable.format(ndefMessage);
                    ndefFormatable.close();
                    return true;
                } else {
                    Log.e(TAG, "The device is not valid type");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "writeTag: Write failed ", e);
        }
        return false;
    }
}
