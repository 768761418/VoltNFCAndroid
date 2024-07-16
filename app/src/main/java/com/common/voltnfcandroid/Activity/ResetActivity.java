package com.common.voltnfcandroid.Activity;

import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import com.common.voltnfcandroid.R;
import com.common.voltnfcandroid.databinding.LayoutResetBinding;

public class ResetActivity extends BaseNfcActivity {

    private LayoutResetBinding layoutResetBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initClick();
    }

    // Initial View Layout
    private void initUI(){
        // Bind Layout
        layoutResetBinding = DataBindingUtil.setContentView(ResetActivity.this, R.layout.layout_reset);
        // Text in the Layout
        SpannableString spannableString = new SpannableString("NOTE : Near Field Communication (NFC) required. To proceed, your mobile device must be touching the lens of the fixture.");

        int startIndex = spannableString.toString().indexOf("NOTE :");
        int endIndex = startIndex + "NOTE :".length();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(boldSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        layoutResetBinding.instructionsThree.setText(spannableString);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!ifNFCUse()) {
            return;
        }
    }

    // Reset Button Trig Activity
    private void initClick(){
        layoutResetBinding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResetActivity.this, NfcWriteActivity.class));
            }
        });
    }

    protected boolean ifNFCUse() {
        if (mNfcAdapter == null) {
            Toast.makeText(this, "No valid Device is Detected", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "No valid Device is Detected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}