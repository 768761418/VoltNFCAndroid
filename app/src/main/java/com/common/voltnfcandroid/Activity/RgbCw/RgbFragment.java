package com.common.voltnfcandroid.Activity.RgbCw;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.common.voltnfcandroid.Data.MsgData;
import com.common.voltnfcandroid.Element.DialogNfcSearch;
import com.common.voltnfcandroid.R;
import com.common.voltnfcandroid.databinding.FragmentRgbBinding;


public class RgbFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

//    日志标签
    private final String TAG = "RgbFragment";
//    布局
    private FragmentRgbBinding fragmentRgbBinding;
//    定义默认信息
    private String msg = MsgData.MSG_HAYWARD;
//    nfc扫描弹窗
    private DialogNfcSearch dialogNfcSearch;
//    数据共享
    private SharedViewModel sharedViewModel;
//    是否开启nfc扫描
    private boolean isNfcUse = false;





    public RgbFragment() {
        // Required empty public constructor
    }

//    nfc处理
    public void handleNewIntent(){

    }
    public static RgbFragment newInstance(String param1, String param2) {
        RgbFragment fragment = new RgbFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        dialogNfcSearch = new DialogNfcSearch(getContext());
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        绑定布局
        fragmentRgbBinding = fragmentRgbBinding.inflate(inflater,container,false);


        initMsgClick();
//        监听数据变化
        initViewModelListener();

        return fragmentRgbBinding.getRoot();
    }


    private void initViewModelListener(){
        sharedViewModel.getReadRgbMsg().observe(getViewLifecycleOwner(), message -> {
            dialogNfcSearch.dismiss();
            // 处理 msg 的变化
            Log.d(TAG, "messages: " + message);
            String readMsg = "";
            switch (message){
                case MsgData.MSG_HAYWARD:
                    readMsg = MsgData.MSG_HAYWARD_TEXT;
                    break;
                case MsgData.MSG_JANDY:
                    readMsg = MsgData.MSG_JANDY_TEXT;
                    break;
                case MsgData.MSG_PENTAIR_POOL:
                    readMsg = MsgData.MSG_PENTAIR_POOL_TEXT;
                    break;
            }
            fragmentRgbBinding.showMsgContent.setText(readMsg);
        });

        sharedViewModel.getType().observe(getViewLifecycleOwner(),type -> {
            if (type == MsgData.TYPE_WRITE_SUCCESS){
                dialogNfcSearch.dismiss();
            }
        });
    }


    /**
     *   初始化点击事件
     **/
    private void initMsgClick(){
//        三个信息切换的点击事件
        fragmentRgbBinding.btnHayward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelect(MsgData.MSG_HAYWARD);
            }
        });

        fragmentRgbBinding.btnJandy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelect(MsgData.MSG_JANDY);
            }
        });

        fragmentRgbBinding.btnPentairPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelect(MsgData.MSG_PENTAIR_POOL);
            }
        });

//        2个按钮的点击事件
        fragmentRgbBinding.btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNfcUse = true;
                dialogNfcSearch.show();

                sharedViewModel.setType(MsgData.TYPE_WRITE_RGB);
                sharedViewModel.setMsg(msg);
                sharedViewModel.setNfcUse(isNfcUse);
            }
        });
        fragmentRgbBinding.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNfcUse = true;
                dialogNfcSearch.show();

                sharedViewModel.setType(MsgData.TYPE_READ_RGB);
                sharedViewModel.setNfcUse(isNfcUse);
            }
        });



//        关闭nfc扫描窗口回调
        dialogNfcSearch.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
//                取消扫描状态
                isNfcUse = false;
                sharedViewModel.setNfcUse(isNfcUse);
            }
        });


    }


    /**
    *   用于修改发送信息和显示选中icon的函数
     **/
    private void showSelect(String msgText){
//        赋值
        msg = msgText;
//        先全隐藏，用谁显示谁
        fragmentRgbBinding.selectHayward.setVisibility(View.INVISIBLE);
        fragmentRgbBinding.selectJandy.setVisibility(View.INVISIBLE);
        fragmentRgbBinding.selectPentairPool.setVisibility(View.INVISIBLE);
//        显示选择
        switch (msg){
            case MsgData.MSG_HAYWARD:
                fragmentRgbBinding.selectHayward.setVisibility(View.VISIBLE);
                break;
            case MsgData.MSG_JANDY:
                fragmentRgbBinding.selectJandy.setVisibility(View.VISIBLE);
                break;
            case MsgData.MSG_PENTAIR_POOL:
                fragmentRgbBinding.selectPentairPool.setVisibility(View.VISIBLE);
                break;
        }


    }

}