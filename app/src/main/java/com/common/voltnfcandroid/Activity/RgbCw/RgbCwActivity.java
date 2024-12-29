package com.common.voltnfcandroid.Activity.RgbCw;

import static com.common.voltnfcandroid.Utils.NfcUtils.createTextRecord;
import static com.common.voltnfcandroid.Utils.NfcUtils.writeTag;
import static com.common.voltnfcandroid.Utils.NfcUtils.readNfcTag;
import static com.common.voltnfcandroid.Utils.NfcUtils.readNfcTagByte;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.io.LineReader;

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
            Log.d(TAG, "isNfcUse: " + isNfcUse);
            this.isNfcUse = isNfcUse;
        });

        sharedViewModel.getMsg().observe(this, message -> {
            // 处理 msg 的变化
            Log.d(TAG, "msg: " + message);
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
                    writeToNfcV(intent,msg,MsgData.TYPE_TAG_RGB);
                    break;
                case MsgData.TYPE_WRITE_CW:
                    writeToNfcV(intent,msg,MsgData.TYPE_TAG_CW);
                    break;
                case MsgData.TYPE_READ_RGB:
                    String readRgbMsg =readFromNfcV(intent,MsgData.TYPE_TAG_RGB);
                    Log.d(TAG, "onNewIntent: " + readRgbMsg);
                    if (!readRgbMsg.equals("")){
                        sharedViewModel.setReadRgbMsg(readRgbMsg);
                    }else {
//                        提示切换到cw模式
                        Toast.makeText(this,getString(R.string.read_mode_cw_fail),Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this,"Please switch to RGBW mode",Toast.LENGTH_SHORT).show();
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
        fragments.add(RgbFragment.newInstance("RGBW Light","0"));
        fragments.add(CwFragment.newInstance("White Light","1"));
        tabLayoutData.add("RGBW Light");
        tabLayoutData.add("White Light");
        //        绑定tabLayout和viewpager2，实现点击切换
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabLayoutData.get(position));
            }
        }).attach();

        //        不允许滑动切换
        viewPager2.setUserInputEnabled(false);
//        懒加载
        viewPager2.setOffscreenPageLimit(1);

//        动态计算fgm的高度
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // 获取当前显示的 Fragment 对应的 View
                View view = fragments.get(position).getView();
                if (view != null){
                    // 调用方法更新 ViewPager2 的高度
                    updatePagerHeightForChild(view, viewPager2);
                }

            }

        });

    }


    /**
     * 更新 ViewPager2 的高度以适应当前 Fragment 的内容高度
     * @param view 当前 Fragment 的根布局
     * @param pager 需要调整高度的 ViewPager2
     */
    private void updatePagerHeightForChild(View view, ViewPager2 pager) {
        // 使用 view.post() 确保 View 已完成渲染，避免测量出错误的高度
        view.post(() -> {
            // 创建宽度的测量规范，使用当前 View 的宽度，并指定为 EXACTLY 模式（完全匹配）
            int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);

            // 创建高度的测量规范，使用 UNSPECIFIED 模式，允许高度动态扩展
            int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            // 测量当前 View 的宽高
            view.measure(wMeasureSpec, hMeasureSpec);

            // 如果当前 ViewPager2 的高度和测量的高度不同，则更新高度
            if (pager.getLayoutParams().height != view.getMeasuredHeight()) {
                // 获取 ViewPager2 的布局参数
                ViewGroup.LayoutParams layoutParams = pager.getLayoutParams();

                // 设置 ViewPager2 的高度为当前 Fragment 的测量高度
                layoutParams.height = view.getMeasuredHeight();

                // 应用新的布局参数x
                pager.setLayoutParams(layoutParams);
            }
        });
    }


    private void writeMessage(Intent intent,String msg){
        Log.d(TAG, "字符串写入NFC");
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

    private void writeToNfcV(Intent intent,String msg,int tagType) {
        Log.d(TAG, "十六进制写入NFC");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            Toast.makeText(this, "No NFC tag detected", Toast.LENGTH_SHORT).show();
            return;
        }
        NfcV nfcV = NfcV.get(tag);
        if (nfcV == null) {
            // 标签不支持 ISO15693
            return;
        }
        try {
            // 连接到标签
            nfcV.connect();

//            构建指令
            byte[][] dataArray = buildInstruction(nfcV,msg,tagType);

//            没有指令直接return
            if (dataArray == null){
                return;
            }

            // 定义写入起始块和块数量
            int startBlock = 3; // 起始块编号
            int blockCount = dataArray.length; // 块数量
//            标识
            int logo = 1;
            // 写入多个块
            for (int i = 0; i < blockCount; i++) {
                byte[] command = MsgData.getWriteCommand(nfcV.getTag().getId(),startBlock,i,dataArray);
//                打印信息
                MsgData.printBytesToHex(command,"RGB写入");
                // 发送命令
                byte[] response = nfcV.transceive(command);
                // 检查响应
                if (response.length > 0 && response[0] == 0x00) {
                    Log.d(TAG, "写入成功！");
//                    通知fragment关闭dialog
                    sharedViewModel.setType(MsgData.TYPE_WRITE_SUCCESS);

                } else {
                    logo = 0;
                    String error = MsgData.printBytesToHex(response,"写入错误");
                    Toast.makeText(this, "Write Message Successfully"+ (response.length > 0 ?error.toString().trim() : "unknow"), Toast.LENGTH_SHORT).show();
                }
            }
            if (logo == 1){
                Toast.makeText(this, getString(R.string.write_success), Toast.LENGTH_SHORT).show();
            }

//            释放资源
            nfcV.close();
            // 写入成功
            System.out.println("Write Success");

        } catch (IOException e) {
            // 写入失败
            System.err.println("Write failed: " + e.getMessage());
        } finally {
            try {
                nfcV.close();
            } catch (IOException e) {
                // 关闭失败
                e.printStackTrace();
            }
        }
    }


    private String readFromNfcV(Intent intent,int tagType){
        String readMsg = "";
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            Toast.makeText(this, "No NFC tag detected", Toast.LENGTH_SHORT).show();
            return readMsg;
        }
        NfcV nfcV = NfcV.get(tag);
        if (nfcV == null) {
            // 标签不支持 ISO15693
            return readMsg;
        }
        try {
            // 连接到标签
            nfcV.connect();

//            读取第五行
            int type = MsgData.getTagMode(nfcV,(byte) 0x05);
            Log.d(TAG, "rgvRead: " + type);
//            如果是rgb模式的读取
            if (tagType == MsgData.TYPE_TAG_RGB){
//                如果是rgb的卡
                if (type == MsgData.TYPE_TAG_RGB){
//                    读第三块的最后一个字符判读类型
                    byte[] dataList = MsgData.getTagData(nfcV,(byte)0x03);
                    MsgData.printBytesToHex(dataList,"rgvRead");
                    if (dataList != null){
                        byte data = dataList[dataList.length - 1];
                        switch (data){
                            case (byte) 0x48:
                                readMsg = MsgData.MSG_HAYWARD_TEXT;
                                break;
                            case (byte) 0x4A:
                                readMsg = MsgData.MSG_JANDY_TEXT;
                                break;
                            case (byte) 0x50:
                                readMsg = MsgData.MSG_PENTAIR_POOL_TEXT;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }


        }
        catch (IOException e) {
            // 写入失败
            System.err.println("Write failed: " + e.getMessage());
        } finally {
            try {
                nfcV.close();
            } catch (IOException e) {
                // 关闭失败
                e.printStackTrace();
            }
        }
        return  readMsg;

    }

//    构建写入指令
//    TODO white的写入逻辑
    private byte[][] buildInstruction(NfcV nfcV,String msg,int tagType) throws IOException {
        byte[][] dataArray ;
        int type = MsgData.getTagMode(nfcV,(byte) 0x05);

//        校验读取模式
        if (tagType == MsgData.TYPE_TAG_RGB){
            //  先校验卡是不是RGB模式或者新卡
            if (type == MsgData.TYPE_TAG_CW){
                Toast toast = Toast.makeText(this,getString(R.string.write_type_fail),Toast.LENGTH_SHORT);
//                View layout =  toast.getView();
//                layout.setBackgroundResource(R.color.btn_reset_text);

                toast.show();
                nfcV.close();
                sharedViewModel.setType(MsgData.TYPE_WRITE_SUCCESS);
                return null;
            }
//            如果通过了校验就构建写入信息
            byte[] dataBlock4;
            switch (msg){
                case MsgData.MSG_HAYWARD:
                    Log.d(TAG, "writeMultipleBlocksToNfcV: 1");
                    dataBlock4 = new byte[]{(byte) 0x6e, (byte) 0xff, (byte) 0xfe, (byte) 0x48};
                    break;
                case MsgData.MSG_JANDY:
                    Log.d(TAG, "writeMultipleBlocksToNfcV: 2");
                    dataBlock4 = new byte[]{(byte) 0x6e, (byte) 0xff, (byte) 0xfe, (byte) 0x4A};
                    break;
                case MsgData.MSG_PENTAIR_POOL:
                    Log.d(TAG, "writeMultipleBlocksToNfcV: 3");
                    dataBlock4 = new byte[]{(byte) 0x6e, (byte) 0xff, (byte) 0xfe, (byte) 0x50};
                    break;
                default:
                    Log.d(TAG, "writeMultipleBlocksToNfcV: 4"+msg);
                    Toast.makeText(this,"Please switch the true message to write to NFC" ,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "数据不正确");
                    return null;
            }
            dataArray = MsgData.getNfcVRgbBytes(dataBlock4);

        } else {
            // 先校验是不是CW模式或者新卡
            if (type == MsgData.TYPE_TAG_RGB){
                Toast.makeText(this,getString(R.string.write_type_fail),Toast.LENGTH_SHORT).show();
                nfcV.close();
                sharedViewModel.setType(MsgData.TYPE_WRITE_SUCCESS);
                return null;
            }
            int temp = 0;
            int bright = 0;
            // 使用 split 方法通过 _ 分割字符串
            String[] parts = msg.split("_");

            try {
                // 分割后的两个字符串
                temp = Integer.parseInt(parts[0]);  // 色温
                bright =Integer.parseInt(parts[1]) ;  // 亮度
            }catch (NumberFormatException e){
                Log.e(TAG, "buildInstruction: ",e);
                Toast.makeText(this,getString(R.string.write_type_fail),Toast.LENGTH_SHORT).show();
                nfcV.close();
                sharedViewModel.setType(MsgData.TYPE_WRITE_SUCCESS);
                return null;
            }



            byte[] dataBlock5 = new byte[]{(byte) 0x00,(byte) temp, (byte) 0x00, (byte) bright};

            dataArray = MsgData.getNfcVCwBytes(dataBlock5);
        }

        return dataArray;

    }





}
