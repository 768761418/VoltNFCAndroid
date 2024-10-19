package com.common.voltnfcandroid.Activity.RgbCw;

import static com.common.voltnfcandroid.Utils.NfcUtils.createTextRecord;
import static com.common.voltnfcandroid.Utils.NfcUtils.writeTag;
import static com.common.voltnfcandroid.Utils.NfcUtils.readNfcTag;
import static com.common.voltnfcandroid.Utils.NfcUtils.readNfcTagByte;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.common.voltnfcandroid.Activity.BaseNfcActivity;
import com.common.voltnfcandroid.Adapter.FgmAdapter;
import com.common.voltnfcandroid.Data.MsgData;
import com.common.voltnfcandroid.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class RgbCwActivity extends BaseNfcActivity {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> tabLayoutData = new ArrayList<>();
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    private final String TAG = "RgbCwActivity";

    private boolean isNfcUse = false;
    private String msg;
    private int type = -1;

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rgb_cw);
        initFgm();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

//        监听fragment的需求和信息变化
        sharedViewModel.getIsNfcUse().observe(this, isNfcUse -> {
            // 处理 isNfcUse 的变化
            Log.d("ViewModel 数据", "isNfcUse: " + isNfcUse);
            this.isNfcUse = isNfcUse;
        });

        sharedViewModel.getMsg().observe(this, message -> {
            // 处理 msg 的变化
            Log.d("ViewModel 数据", "msg: " + message);
            this.msg = message;
        });

        sharedViewModel.getType().observe(this,type -> {
            this.type = type;
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        判断当前显示的fragement是哪个，然后各自执行nfc操作
        if (isNfcUse){
            switch (type){
                case MsgData.TYPE_WRITE_RGB:
                case MsgData.TYPE_WRITE_CW:
                    writeMessage(intent,msg);
                    break;
                case MsgData.TYPE_READ_RGB:
                    String readRgbMsg = readNfcTag(intent);
                    if (!readRgbMsg.equals(MsgData.MSG_HAYWARD)
                            && !readRgbMsg.equals(MsgData.MSG_JANDY)
                            && !readRgbMsg.equals(MsgData.MSG_PENTAIR_POOL)){
                        sharedViewModel.setReadRgbMsg("");
                    }else {
                        sharedViewModel.setReadRgbMsg(readRgbMsg);
                    }
                    break;
                case MsgData.TYPE_READ_CW:
//                    读取字节数据
                    byte[] bytes = readNfcTagByte(intent);
//                    解析数据
                    String readCwMsg = MsgData.getReadLuminanceAndTempValue(bytes);
                    if (readCwMsg != null && !readCwMsg.equals("")){
                        //                    通知fragement修改数据
                        sharedViewModel.setReadCwMsg(readCwMsg);
                    }else {
                        sharedViewModel.setReadCwMsg("");
                    }
                    Log.d(TAG, "onNewIntent: " +readCwMsg);

                    break;
            }

        }

    }

    private void initFgm(){
//        绑定viewpager和切换栏
        viewPager2 = findViewById(R.id.index_viewPager);
        tabLayout  = findViewById(R.id.index_tab_layout);
        FgmAdapter fgmAdapter = new FgmAdapter(getSupportFragmentManager(),getLifecycle(),fragments);
        viewPager2.setAdapter(fgmAdapter);
        fragments.add(RgbFragment.newInstance("RGBW","0"));
        fragments.add(CwFragment.newInstance("CW","1"));
        tabLayoutData.add("RGBW");
        tabLayoutData.add("CW");
        //        绑定tabLayout和viewpager2，实现点击切换
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabLayoutData.get(position));
            }
        }).attach();

        //        不允许滑动切换
        viewPager2.setUserInputEnabled(false);
    }


    private void writeMessage(Intent intent,String msg){
        if (msg == null)
            return;
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag == null) {
            Toast.makeText(this, "No NFC tag detected", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "writeMessage: " + detectedTag);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createTextRecord(msg)});
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "Write Message Successfully", Toast.LENGTH_SHORT).show();
//            通知fragement关闭dialog
            sharedViewModel.setType(MsgData.TYPE_WRITE_SUCCESS);

        } else {
            Toast.makeText(this, "Write Message Failed", Toast.LENGTH_SHORT).show();
        }
    }

}
