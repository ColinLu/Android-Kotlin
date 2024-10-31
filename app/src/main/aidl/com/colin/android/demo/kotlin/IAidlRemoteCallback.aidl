// IAidlRemoteCallback.aidl
package com.colin.android.demo.kotlin;

import com.colin.android.demo.kotlin.def.ItemBean;
// Declare any non-default types here with import statements

oneway interface IAidlRemoteCallback {
     void aidlChanged(String data);
     void itemChanged(in ItemBean itembean);
}