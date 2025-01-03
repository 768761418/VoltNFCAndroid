package com.common.voltnfcandroid.Utils;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

public class NfcUtils {
    private final static  String TAG = "NfcUtils";

    /**
     * 创建NDEF文本数据
     *
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
        // 使用UTF-16编码
        Charset utfEncoding = Charset.forName("UTF-16LE");
        // 将文本转换为UTF-16格式
        byte[] textBytes = text.getBytes(utfEncoding);

        // 创建 UTF-16 小端字节顺序的 BOM（字节顺序标记）
        byte[] bom = new byte[] { (byte) 0xFF, (byte) 0xFE };  // UTF-16LE BOM

        // 设置状态字节编码最高位数为0 (表示UTF-16编码)
        int utfBit = 0x80;
        // 定义状态字节
        char status = (char) (utfBit + langBytes.length);

        // 创建最终的数据数组，长度为状态字节 + 语言字节 + BOM + 文本字节
        byte[] data = new byte[1 + langBytes.length + bom.length + textBytes.length];

        // 设置状态字节
        data[0] = (byte) status;

        // 设置语言编码字节
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);

        // 将 BOM 添加到数据中
        System.arraycopy(bom, 0, data, 1 + langBytes.length, bom.length);

        // 设置文本字节
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length + bom.length, textBytes.length);

        // 创建 NdefRecord
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);

        // 将 NdefRecord 转化为字节数组
        byte[] ndefBytes = ndefRecord.toByteArray();

        // 打印字节数组的内容（十六进制格式）
        StringBuilder sb = new StringBuilder();
        for (byte b : ndefBytes) {
            sb.append(String.format("%02X ", b));
        }
        Log.d("NdefRecord", "NDEF Record 内容: " + sb.toString());

        return ndefRecord;
//        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
//        // 使用UTF-16编码
//        Charset utfEncoding = Charset.forName("UTF-16");
//        // 将文本转换为UTF-16格式
//        byte[] textBytes = text.getBytes(utfEncoding);
//        // 设置状态字节编码最高位数为1 (表示UTF-16编码)
//        int utfBit = 0x80;  // 最高位为1，表示UTF-16
//        //定义状态字节
//        char status = (char) (utfBit + langBytes.length);
//        byte[] data = new byte[1 + langBytes.length + textBytes.length];
//        //设置第一个状态字节，先将状态码转换成字节
//        data[0] = (byte) status;
//        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
//        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
//        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
//        //到textBytes.length的位置
//        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
//        //通过字节传入NdefRecord对象
//        //NdefRecord.RTD_TEXT：传入类型 读写
//        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
//                NdefRecord.RTD_TEXT, new byte[0], data);
//
//        // 将 NdefRecord 转化为字节数组
//        byte[] ndefBytes = ndefRecord.toByteArray();
//
//        // 打印字节数组的内容（十六进制格式）
//        StringBuilder sb = new StringBuilder();
//        for (byte b : ndefBytes) {
//            sb.append(String.format("%02X ", b));
//        }
//        Log.d("NdefRecord", "NDEF Record 内容: " + sb.toString());
//        return ndefRecord;
    }

// 静态方法，用于向 NFC 标签写入 NDEF 消息
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            // 获取 Ndef 实例，表示当前标签是否支持 NDEF
            Ndef ndef = Ndef.get(tag);

            // 如果标签支持 NDEF
            if (ndef != null) {
                ndef.connect();  // 连接标签
                ndef.writeNdefMessage(ndefMessage);  // 写入 NDEF 消息
                ndef.close();  // 关闭连接
                return true;  // 写入成功，返回 true
            } else {
                // 如果标签不支持 NDEF，尝试将其格式化为 NDEF 格式
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                // 如果标签可以格式化为 NDEF
                if (ndefFormatable != null) {
                    ndefFormatable.connect();  // 连接标签
                    ndefFormatable.format(ndefMessage);  // 格式化标签并写入 NDEF 消息
                    ndefFormatable.close();  // 关闭连接
                    return true;  // 写入成功，返回 true
                } else {
                    // 如果设备既不支持 NDEF，也不能格式化为 NDEF
                    Log.e(TAG, "The device is not valid type");  // 打印错误日志
                }
            }
        } catch (Exception e) {
            // 捕获所有异常，并打印错误信息
            Log.e(TAG, "writeTag: Write failed ", e);
        }
        return false;  // 写入失败，返回 false
    }


    /**
     * 读取NFC标签文本数据
     */
    public static String readNfcTag(Intent intent) {
        String mTagText = "";
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
//                    mTagText += textRecord + "\n\ntext\n" + contentSize + " bytes";
                    mTagText += textRecord;

                }
            } catch (Exception e) {
            }
        }
        return mTagText;
    }

    /**
     * 读取NFC标签的字节数据
     */
    public static byte[] readNfcTagByte(Intent intent) {
        byte[] nfcBytes = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    nfcBytes = record.getPayload();  // 获取字节数据
                }
            } catch (Exception e) {
                Log.e("NFC_Bytes", "Error reading NFC tag bytes", e);
            }
        }
        return nfcBytes;
    }




    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     *
     * @param ndefRecord
     * @return
     */
    public static String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }




}
