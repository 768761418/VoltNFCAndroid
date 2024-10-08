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
    private final MutableLiveData<String> readMsg = new MutableLiveData<>();

        // 更新 isNfcUse 的方法
    public void setNfcUse(boolean nfcUse) {
        isNfcUse.setValue(nfcUse);
    }

    // 获取 isNfcUse 的 LiveData
    public LiveData<Boolean> getIsNfcUse() {
        return isNfcUse;
    }

    // 更新 msg 的方法
    public void setMsg(String message) {
        msg.setValue(message);
    }

    // 获取 msg 的 LiveData
    public LiveData<String> getMsg() {
        return msg;
    }

    // 更新 读取数据 的方法
    public void setReadMsg(String message) {
        readMsg.setValue(message);
    }

    // 获取 读取数据 的 LiveData
    public LiveData<String> getReadMsg() {
        return readMsg;
    }

    // 更新 type 的方法
    public void setType(int newType) {
        type.setValue(newType);
    }

    // 获取 type 的 LiveData
    public LiveData<Integer> getType() {
        return type;
    }




}

