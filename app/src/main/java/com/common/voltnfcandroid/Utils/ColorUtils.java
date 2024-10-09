package com.common.voltnfcandroid.Utils;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.widget.ImageView;

public class ColorUtils {

    //    初始化颜色预设
    public static void UtilsChangeColor(int color, ImageView imageview, Boolean isCurrent){
        //创建Drawable对象
        GradientDrawable drawable=new GradientDrawable();
        if (isCurrent){
            //设置圆角大小
            drawable.setCornerRadius(10);
        }else {
            //设置shape形状
            drawable.setShape(GradientDrawable.OVAL);
        }
        //设置背景色
        drawable.setColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageview.setBackground(drawable);
        }
    }
}
