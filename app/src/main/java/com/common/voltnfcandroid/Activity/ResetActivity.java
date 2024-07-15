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

    private void initUI(){
        layoutResetBinding = DataBindingUtil.setContentView(ResetActivity.this, R.layout.layout_reset);
// 创建一个SpannableString对象
        SpannableString spannableString = new SpannableString("NOTE : Near Field Communication (NFC) required. To proceed, your mobile device must be touching the lens of the fixture.");

// 设置"NOTE :"部分的颜色和样式
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

    private void initClick(){
        layoutResetBinding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ResetActivity.this, NfcWriteActivity.class);
//                startActivity(intent);
                startActivity(new Intent(ResetActivity.this, NfcWriteActivity.class));
            }
        });
    }

    /**
     * 检测工作,判断设备的NFC支持情况
     *
     * @return
     */
    protected boolean ifNFCUse() {
        if (mNfcAdapter == null) {
            Toast.makeText(this, "没有nfc", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "没有nfc", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}