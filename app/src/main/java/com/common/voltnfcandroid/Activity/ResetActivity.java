package com.common.voltnfcandroid.Activity;

import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
        // HTML 字符串
        String htmlText = "<b>NOTE :</b> Near Field Communication (NFC) required. To proceed, your mobile device must be touching the lens of the fixture.";

        // 使用 Html.fromHtml() 方法解析 HTML 字符串
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            layoutResetBinding.instructionsThree.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
        }
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