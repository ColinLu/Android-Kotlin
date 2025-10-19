// IAIDLCallback.aidl
package com.colin.android.demo.kotlin;

// Declare any non-default types here with import statements
import com.colin.android.demo.kotlin.ItemBean;
/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025/10/18 16:53
 *
 * Des   :AIDL客服端接口定义
 */
oneway interface IAIDLCallback {
     void aidlStatus(in boolean isConnected);
     void aidlChanged(String data);
     void itemChanged(in ItemBean item);
}