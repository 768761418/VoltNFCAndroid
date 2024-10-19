package com.common.voltnfcandroid.Activity.RgbCw;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.voltnfcandroid.Data.MsgData;
import com.common.voltnfcandroid.Element.DialogNfcSearch;
import com.common.voltnfcandroid.R;
import com.common.voltnfcandroid.Utils.ColorUtils;
import com.common.voltnfcandroid.databinding.FragmentCwBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CwFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CwFragment extends Fragment {
//    fragement创建默认创建参数
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
//    日志标签
    private final String TAG = "CwFragment";
//    布局
    private FragmentCwBinding fragmentCwBinding;
//    viewModel数据交互
    private SharedViewModel sharedViewModel;
//    nfc扫描弹窗
    private DialogNfcSearch dialogNfcSearch;
    //    是否开启nfc扫描
    private boolean isNfcUse = false;
    //    定义默认信息
    private String msg = MsgData.MSG_HAYWARD;
//    色温值
    int[] temperatureValues = {2700, 3000, 4000, 5000, 5700};
//    亮度值,0,50%,100%,25%,40%,75% 值为100
    int[] luminanceValues = {0,50,100,25,40,75};
    


    public CwFragment() {
        // Required empty public constructor
    }
    public static CwFragment newInstance(String param1, String param2) {
        CwFragment fragment = new CwFragment();
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
            // 初始化viewModel和dialog
            dialogNfcSearch = new DialogNfcSearch(getContext());
            sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        绑定布局
        fragmentCwBinding = fragmentCwBinding.inflate(inflater,container,false);
        initColor();
        initClick();
        initViewModelListener();

        return fragmentCwBinding.getRoot();
    }


    private void initColor(){
//    色温颜色值
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            色温块颜色
            int[] temperatureColors = {
                    getContext().getColor(R.color.temperature_color_1),
                    getContext().getColor(R.color.temperature_color_2),
                    getContext().getColor(R.color.temperature_color_3),
                    getContext().getColor(R.color.temperature_color_4),
                    getContext().getColor(R.color.temperature_color_5)
            };
            //    色温布局值
            ImageView[] temperatureImg = {
                    fragmentCwBinding.cwPresuppose1,
                    fragmentCwBinding.cwPresuppose2,
                    fragmentCwBinding.cwPresuppose3,
                    fragmentCwBinding.cwPresuppose4,
                    fragmentCwBinding.cwPresuppose5
            };
//            初始化色温块的颜色
            for (int i = 0; i < temperatureImg.length; i++) {
                ColorUtils.UtilsChangeColor(temperatureColors[i],temperatureImg[i],false);
            }


        }


    }


    private void initClick(){
        LinearLayout[] linears = new LinearLayout[]{
                fragmentCwBinding.temperature1,
                fragmentCwBinding.temperature2,
                fragmentCwBinding.temperature3,
                fragmentCwBinding.temperature4,
                fragmentCwBinding.temperature5,
        };
//        定义色温的点击事件
        for (int i = 0; i < temperatureValues.length ; i++) {
            int finalI = i;
            linears[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fragmentCwBinding.cwTemperatureSeekbar.setProgress(temperatureValues[finalI],true);
                    }
                }
            });
        }
//        定义亮度按钮的点击事件
        TextView[] luminanceBtn = {
                fragmentCwBinding.luminance0,
                fragmentCwBinding.luminance50,
                fragmentCwBinding.luminance100,
                fragmentCwBinding.luminance25,
                fragmentCwBinding.luminance40,
                fragmentCwBinding.luminance75
        };

        for (int i = 0; i < luminanceBtn.length; i++) {
            int finalI = i;
            luminanceBtn[i].setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    fragmentCwBinding.luminanceSeekbar.setProgress(luminanceValues[finalI],true);
                }
            });
        }

//        read and write
        fragmentCwBinding.btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                获取当前的配置情况
                int temp = fragmentCwBinding.cwTemperatureSeekbar.getProgress();
                int bright = fragmentCwBinding.luminanceSeekbar.getProgress();
//                获取写入信息
                msg = MsgData.getWriteLuminanceAndTempValue(temp,bright);

                isNfcUse = true;
                dialogNfcSearch.show();
//                通知Activity扫描NFC
                sharedViewModel.setType(MsgData.TYPE_WRITE_CW);
                sharedViewModel.setMsg(msg);
                sharedViewModel.setNfcUse(isNfcUse);
            }
        });

        fragmentCwBinding.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNfcUse = true;
                dialogNfcSearch.show();
                // 通知Activity扫描NFC
                sharedViewModel.setType(MsgData.TYPE_READ_CW);
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

    private void initViewModelListener(){
        sharedViewModel.getReadCwMsg().observe(getViewLifecycleOwner(), message -> {
            dialogNfcSearch.dismiss();
            // 处理 msg 的变化
            Log.d("ViewModel 数据", "msg: " + message);
            // 使用 split 方法通过 _ 分割字符串
            String[] parts = message.split("_");

            // 分割后的两个字符串
            String temp = parts[0];  // 色温
            String bright = parts[1];  // 亮度


            fragmentCwBinding.showMsgBright.setText(bright);
            fragmentCwBinding.showMsgTemp.setText(temp);
        });

        sharedViewModel.getType().observe(getViewLifecycleOwner(),type -> {
            if (type == MsgData.TYPE_WRITE_SUCCESS){
                dialogNfcSearch.dismiss();
            }
        });
    }


}

