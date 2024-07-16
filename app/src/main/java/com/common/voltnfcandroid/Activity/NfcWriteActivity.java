package com.common.voltnfcandroid.Activity;

import static com.common.voltnfcandroid.Utils.NfcUtils.createTextRecord;
import static com.common.voltnfcandroid.Utils.NfcUtils.writeTag;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.common.voltnfcandroid.R;
import com.common.voltnfcandroid.databinding.LayoutNfcWriteBinding;

public class NfcWriteActivity extends BaseNfcActivity{
    private String mText = "Reset";
    private LayoutNfcWriteBinding layoutNfcWriteBinding;

    private String TAG ="NfcWriteActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    // Second View Layout
    private void initUI(){
        // Bind Layout
        layoutNfcWriteBinding = DataBindingUtil.setContentView(NfcWriteActivity.this, R.layout.layout_nfc_write);
        // The rotating circle icon
        Glide.with(this).
                load(R.drawable.loading).
                into(layoutNfcWriteBinding.iconSearching);
        initClick();
    }
    private void initClick(){
        layoutNfcWriteBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NfcWriteActivity.this, "Cancel Searching", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        writeMessage(intent);
    }


    private void writeMessage(Intent intent){
        if (mText == null)
            return;
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag == null) {
            Toast.makeText(this, "No NFC tag detected", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "writeMessage: " + detectedTag);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createTextRecord(mText)});
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "Reset Successful", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Reset Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
