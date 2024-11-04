// IDemoAidlInterface.aidl
package com.colin.android.demo.kotlin;

import com.colin.android.demo.kotlin.IAidlRemoteCallback;
import com.colin.android.demo.kotlin.ItemBean;

interface IDemoAidlInterface {
    void register(IAidlRemoteCallback callback);
    void unregister(IAidlRemoteCallback callback);
    void stringChanged(String string);
    void itemChanged(in item ItemBean);
}