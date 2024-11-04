// IAidlRemoteCallback.aidl
package com.colin.android.demo.kotlin;

// Declare any non-default types here with import statements
import com.colin.android.demo.kotlin.ItemBean;

oneway interface IAidlRemoteCallback {
     void aidlChanged(String data);
     void itemChanged(in item ItemBean);
}