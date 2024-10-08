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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RgbFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RgbFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
//    日志标签
    private final String TAG = "RgbFragment";
//    布局
    private FragmentRgbBinding fragmentRgbBinding;
//    定义默认信息
    private String msg = MsgData.MSG_HAYWARD;
    private DialogNfcSearch dialogNfcSearch;
//    数据共享
    private SharedViewModel sharedViewModel;
    private boolean isNfcUse = false;



    private String mParam1;
    private String mParam2;

    public RgbFragment() {
        // Required empty public constructor
    }

//    nfc处理
    public void handleNewIntent(){

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RgbFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        绑定布局
        fragmentRgbBinding = fragmentRgbBinding.inflate(inflater,container,false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        initMsgClick();
        initViewModelListener();
        return fragmentRgbBinding.getRoot();
    }


    private void initViewModelListener(){
        sharedViewModel.getReadMsg().observe(getViewLifecycleOwner(), message -> {

            dialogNfcSearch.dismiss();
            // 处理 msg 的变化
            Log.d("ViewModel 数据", "msg: " + message);
            fragmentRgbBinding.showMsgContent.setText(message);
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

//        三个按钮的点击事件
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
        fragmentRgbBinding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNfcUse = true;
                dialogNfcSearch.show();

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