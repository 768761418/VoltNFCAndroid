package com.common.voltnfcandroid.Activity;

import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import com.common.voltnfcandroid.R;
import com.common.voltnfcandroid.databinding.LayoutResetBinding;

public class ResetActivity extends BaseActivity {

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
    }

    private void initClick(){
        layoutResetBinding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetActivity.this,NfcWriteActivity.class);
                startActivity(intent);
            }
        });
    }
}