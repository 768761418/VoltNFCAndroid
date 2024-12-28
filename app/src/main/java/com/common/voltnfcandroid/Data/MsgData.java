package com.common.voltnfcandroid.Data;

import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class MsgData {

    public static final String TAG = "MsgData";

    public static final int TYPE_WRITE_RGB = 1000;
    public static final int TYPE_READ_RGB = 1001;
    public static final int TYPE_WRITE_CW = 1010;
    public static final int TYPE_READ_CW = 1011;
    public static final int TYPE_WRITE_SUCCESS = 1100;

    public static final int TYPE_TAG_RGB = 1110;
    public static final int TYPE_TAG_CW = 1111;
    public static final int TYPE_TAG_DEFAULT = 1112;

    public static final String MSG_HAYWARD = "Haywa";
    public static final String MSG_JANDY = "Jaywa";
    public static final String MSG_PENTAIR_POOL = "Paywa";


    public static final String MSG_HAYWARD_TEXT = "Hayward";
    public static final String MSG_JANDY_TEXT = "Jandy";
    public static final String MSG_PENTAIR_POOL_TEXT = "Pentair";

    /**
     * 获取色温和亮度的nfc写入信息
     * input 色温值temp 亮度值 bright
     * @returned msg:String
     * */
    public static String getWriteLuminanceAndTempValue(int temp , int bright){
        // 将温度转换为UInt8并计算
        char tempChar = (char)((temp - 2700) * 100 / 3800);
        char brightChar = (char)bright;
        Log.d(TAG, "demo: " + tempChar + "//" + brightChar);

        // 拼接成字符串
        String msg = "1" + tempChar + brightChar;
        return  msg;
    }


    public static String getReadLuminanceAndTempValue(byte[] bytes){
        String value = null;
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

        if (bytes[5] == 0x31){
            int tempValue = (bytes[7]* 38) + 2700;
            String brightValue = bytes[9] + "%";
            Log.d(TAG, "getReadLuminanceAndTempValue: " + tempValue + "k");
            switch (tempValue){
//                case 3450:
                case 3042:
                    value = "3000k_" + brightValue;
                    break;
                case 4486:
                    value = "4000k_" + brightValue;
                    break;
                case 5588:
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


//    打印字节数据
    public static String printBytesToHex(byte[] bytes ,String logo) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        Log.d(TAG, logo+": " + sb);
        return sb.toString();
    }


    public static byte[][] getNfcVRgbBytes(byte[] dataBlock4) {
        byte[] dataBlock5 = new byte[]{(byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x79};
        byte[] dataBlock6 = new byte[]{(byte) 0x50, (byte) 0x50, (byte) 0x50, (byte) 0x50};
        byte[] dataBlock7 = new byte[]{(byte) 0x50, (byte) 0x50, (byte) 0x50, (byte) 0x50};
        byte[][] dataArray = new byte[][]{dataBlock4, dataBlock5, dataBlock6, dataBlock7};
        return dataArray;
    }

    public static byte[][] getNfcVCwBytes(byte[] dataBlock4) {
        byte[] dataBlock5 = new byte[]{(byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x79};
        byte[] dataBlock6 = new byte[]{(byte) 0x50, (byte) 0x50, (byte) 0x50, (byte) 0x50};
        byte[] dataBlock7 = new byte[]{(byte) 0x50, (byte) 0x50, (byte) 0x50, (byte) 0x50};
        byte[][] dataArray = new byte[][]{dataBlock4, dataBlock5, dataBlock6, dataBlock7};
        return dataArray;
    }

    //    构建nfc写入指令
    public static byte[] getWriteCommand(byte[] tagId , int startBlock,int index,byte[][] dataArray){
        // 构造ISO15693的写单块命令 (0x21 for WriteSingleBlock)
        byte[] command = new byte[11 + 4 ];
        command[0] = 0x22; // 标志位（高数据速率，带地址）
        command[1] = 0x21; // 命令码（写单块）
        System.arraycopy(tagId, 0, command, 2, 8); // UID
        command[10] = (byte) (startBlock + index); // 块号
        System.arraycopy(dataArray[index], 0, command, 11, 4); // 数据
        return  command;
    }

    //    构建nfc读取指令
    public static int getTagMode(NfcV nfcV, byte blockIndex) throws IOException {
        // 构造寻址模式命令
        byte[] command = new byte[11];
        command[0] = 0x22; // Flags: Addressed Mode
        command[1] = 0x20; // Command: Read single block
        System.arraycopy(nfcV.getTag().getId(), 0, command, 2, nfcV.getTag().getId().length); // 添加 UID
        command[10] = blockIndex; // Block number
        // 发送命令并获取响应
        byte[] response = nfcV.transceive(command);
        MsgData.printBytesToHex(response,"readNfc");
        if (response != null && response[0] == 0x00) {
            byte[] data = Arrays.copyOfRange(response, 1, response.length);
            byte type = data[data.length - 1];
            switch (type){
                case (byte) 0x31:
                    return MsgData.TYPE_TAG_CW;
                case (byte) 0x50:
                    return MsgData.TYPE_TAG_RGB;
                default:
                    return MsgData.TYPE_TAG_DEFAULT;
            }

        }
        return MsgData.TYPE_TAG_DEFAULT;
    }

    public static byte[] getTagData(NfcV nfcV, byte blockIndex) throws IOException {
        byte[] command = new byte[3];
        command[0] = 0x02; // Flags: Non-addressed Mode
        command[1] = 0x20; // Command: Read single block
        command[2] = blockIndex; // Block number: 7
        // 发送命令并获取响应
        byte[] response = nfcV.transceive(command);
        if (response != null && response[0] == 0x00) {
            byte[] dataList = Arrays.copyOfRange(response, 1, response.length);
            return dataList;
        }
        return null;
    }


}
