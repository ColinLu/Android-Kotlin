// IDemoAidlInterface.aidl
package com.colin.android.demo.kotlin;

import com.colin.android.demo.kotlin.IAidlRemoteCallback;
import com.colin.android.demo.kotlin.def.ItemBean;

interface IDemoAidlInterface {
    void register(IAidlRemoteCallback callback);
    void unregister(IAidlRemoteCallback callback);
    void stringChanged(String string);
    void itemChanged(inout ItemBean itembean);
}