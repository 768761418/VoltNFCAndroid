package com.common.voltnfcandroid.Activity.RgbCw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    // 传递是否扫描
    private final MutableLiveData<Boolean> isNfcUse = new MutableLiveData<>();

    // 传递写入的信息
    private final MutableLiveData<String> msg = new MutableLiveData<>();

    //传递扫描类型，读还是写
    private final MutableLiveData<Integer> type = new MutableLiveData<>();

    // 传递读取的数据
    private final MutableLiveData<String> readRgbMsg = new MutableLiveData<>();
    // 传递读取的数据
    private final MutableLiveData<String> readCwMsg = new MutableLiveData<>();

    //  使用nfc
    public void setNfcUse(boolean nfcUse) {
        isNfcUse.setValue(nfcUse);
    }
    public LiveData<Boolean> getIsNfcUse() {
        return isNfcUse;
    }

//    发送信息
    public void setMsg(String message) {
        msg.setValue(message);
    }
    public LiveData<String> getMsg() {
        return msg;
    }

    // 读取RGB数据
    public void setReadRgbMsg(String message) {
        readRgbMsg.setValue(message);
    }
    public LiveData<String> getReadRgbMsg() {
        return readRgbMsg;
    }

    // 读取CW数据
    public void setReadCwMsg(String message) {
        readCwMsg.setValue(message);
    }
    public LiveData<String> getReadCwMsg() {
        return readCwMsg;
    }

    // 更新 type 的方法
    public void setType(int newType) {
        type.setValue(newType);
    }
    public LiveData<Integer> getType() {
        return type;
    }




}

