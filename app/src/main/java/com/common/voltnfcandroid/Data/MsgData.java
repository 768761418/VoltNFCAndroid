package com.common.voltnfcandroid.Data;

import android.util.Log;

import java.nio.charset.Charset;
import java.util.Arrays;

public class MsgData {

    public static final String TAG = "MsgData";

    public static final int TYPE_WRITE_RGB = 1000;
    public static final int TYPE_READ_RGB = 1001;
    public static final int TYPE_WRITE_CW = 1010;
    public static final int TYPE_READ_CW = 1011;
    public static final int TYPE_WRITE_SUCCESS = 1100;

    public static final String MSG_HAYWARD = "Hayward";
    public static final String MSG_JANDY = "Jandy";
    public static final String MSG_PENTAIR_POOL = "pentair";


    /**
     * 获取色温和亮度的nfc写入信息
     * input 色温值temp 亮度值 bright
     * @returned msg:String
     * */
    public static String getWriteLuminanceAndTempValue(int temp , int bright){
        // 将温度转换为UInt8并计算
        char tempChar = (char)((temp - 2700) * 100 / 3000);
        char brightChar = (char)bright;
        Log.d(TAG, "demo: " + tempChar + "//" + brightChar);

        // 拼接成字符串
        String msg = "1" + tempChar + brightChar;
        return  msg;
    }


    public static String getReadLuminanceAndTempValue(byte[] bytes){
        String value = "";
        // 确保字节数组不为空
        if (bytes == null || bytes.length == 0) {
            return value; // 返回默认值
        }
        Log.d(TAG, "getReadLuminanceAndTempValue: " + Arrays.toString(bytes) );
        StringBuilder sb = new StringBuilder("Bytes: ");
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b)); // 以十六进制格式打印
        }
        Log.d(TAG, "getReadLuminanceAndTempValue: " + sb.toString());

        if (bytes[6] == 0x31){
            int tempValue = (bytes[8]* 30) + 2700;
            String brightValue = bytes[10] + "%";
            switch (tempValue){
                case 3450:
                    value = "3000k_" + brightValue;
                    break;
                case 4530:
                    value = "4000k_" + brightValue;
                    break;
                case 5310:
                    value = "5000k_" + brightValue;
                    break;
                default:
                    value = tempValue+"k_" + brightValue;
                    break;

            }
        }

//        // 将字符转换为int值
//        int temp = (int) tempChar * 3000 / 100 + 2700; // 反向计算温度
//        int bright = (int) brightChar; // 亮度直接转换为int
//
//        Log.d(TAG, "Extracted Temp: " + temp + ", Bright: " + bright);
//
        return value; // 返回默认值
    }



}
