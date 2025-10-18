// IDemoAidlCallback.aidl
package com.colin.android.demo.kotlin;

// Declare any non-default types here with import statements
import com.colin.android.demo.kotlin.def.ItemBean;

oneway interface IDemoAidlCallback {
     void aidlStatus(in boolean isConnected);
     void aidlChanged(String data);
     void itemChanged(in ItemBean item);
}