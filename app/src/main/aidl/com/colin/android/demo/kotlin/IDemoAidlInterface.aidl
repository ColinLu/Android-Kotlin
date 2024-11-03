// IDemoAidlInterface.aidl
package com.colin.android.demo.kotlin;

import com.colin.android.demo.kotlin.IAidlRemoteCallback;

interface IDemoAidlInterface {
    void register(IAidlRemoteCallback callback);
    void unregister(IAidlRemoteCallback callback);
    void stringChanged(String string);
}