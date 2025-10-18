// IDemoAidlInterface.aidl
package com.colin.android.demo.kotlin;

import com.colin.android.demo.kotlin.IDemoAidlCallback;
import com.colin.android.demo.kotlin.def.ItemBean;

interface IDemoAidlInterface {
    void register(IDemoAidlCallback callback);
    void unregister(IDemoAidlCallback callback);
    void aidlStatus(in boolean isConnected);
    void stringChanged(String string);
    void itemChanged(in ItemBean item);
}