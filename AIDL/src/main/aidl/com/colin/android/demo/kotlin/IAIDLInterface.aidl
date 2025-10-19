// IAIDLInterface.aidl
package com.colin.android.demo.kotlin;

import com.colin.android.demo.kotlin.IAIDLCallback;
import com.colin.android.demo.kotlin.ItemBean;
/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025/10/18 16:52
 *
 * Des   :AIDL服务端接口定义
 */
interface IAIDLInterface {
    void register(IAIDLCallback callback);
    void unregister(IAIDLCallback callback);
    void aidlStatus(in boolean isConnected);
    void stringChanged(String string);
    void itemChanged(in ItemBean item);
}