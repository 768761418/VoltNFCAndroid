package com.common.voltnfcandroid.Element;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.common.voltnfcandroid.R;

public class DialogNfcSearch extends Dialog {

    ImageView searchGif;
    public DialogNfcSearch(@NonNull Context context) {
        super(context);
    }

    public DialogNfcSearch(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogNfcSearch(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nfc_search);
        initUi();
    }

    private void initUi(){
//        加载扫描gif动画
        searchGif = findViewById(R.id.gif_searching);
        Glide.with(getContext()).
                load(R.drawable.gif_nfc_search).
                into(searchGif);
        // 设置对话框的动画效果
        getWindow().setWindowAnimations(R.style.dialog_animation);
        // 设置对话框的宽度
        Window window = getWindow();
        // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        // 设置宽度
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        // 给 DecorView 设置背景颜色，很重要，不然导致 Dialog 内容显示不全，有一部分内容会充当 padding，上面例子有举出
//        window.getDecorView().setBackgroundColor(Color.WHITE);
        window.getDecorView().setBackgroundResource(R.drawable.bg_msg_layout);
        window.setAttributes(layoutParams);
    }
}
