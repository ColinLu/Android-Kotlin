**AIDL 是 Android 提供的一种工具，用于在不同的进程之间通信，特别适合在服务端与客户端之间传递复杂的数据结构。**

实现步骤：

##### 1. **定义 AIDL 接口文件**：

###### 1.1 新建一个 服务端需要实现的`IAIDLInterface.aidl` 文件。
实现此`IAIDLInterface.aidl`文件一般在后台服务端

   ```aidl
        // IAIDLInterface.aidl
        package com.colin.android.demo.kotlin;
        import com.colin.android.demo.kotlin.IAIDLCallback;
        import com.colin.android.demo.kotlin.ItemBean;
        interface IAIDLInterface {
            void register(IAIDLCallback callback);
            void unregister(IAIDLCallback callback);
            void aidlStatus(in boolean isConnected);
            void stringChanged(String string);
            void itemChanged(in ItemBean item);
        }
   ```

###### 1.2 新建一个 客服端需要实现的`IAIDLCallback.aidl` 文件。
实现此`IAIDLCallback.aidl`文件一般在界面展示的客服端

   ```aidl
        // IAIDLCallback.aidl
        package com.colin.android.demo.kotlin;
        // Declare any non-default types here with import statements
        import com.colin.android.demo.kotlin.ItemBean;
        oneway interface IAIDLCallback {
             void aidlStatus(in boolean isConnected);
             void aidlChanged(String data);
             void itemChanged(in ItemBean item);
        }
   ```
###### 1.3 新建一个服务端和客服端需要传输的自定义数据结构（可选）

   ```aidl
        // ItemBean.aidl
        package com.colin.android.demo.kotlin;
        import com.colin.android.demo.kotlin.ItemBean;
        parcelable ItemBean;
   ```
##### 2. **实现 AIDL 接口**：

###### 2.1 实现服务端的 `IAIDLInterface.aidl`
   在 `Service` 中提供一个 `IBinder` 实现。

   ```kotlin
        package com.colin.android.demo.kotlin.service
        import android.app.Service
        import android.content.Intent
        import android.os.IBinder
        import android.os.RemoteCallbackList
        import android.os.RemoteException
        import android.util.Log
        import com.colin.android.demo.kotlin.IAIDLCallback
        import com.colin.android.demo.kotlin.IAIDLInterface
        import com.colin.android.demo.kotlin.ItemBean

        class AIDLService : Service() {
            companion object {
                private const val TAG = "AIDLService"
                const val ACTION = "com.colin.android.demo.kotlin.service.AIDLService"
            }
        
            private val callbackList by lazy { RemoteCallbackList<IAIDLCallback>() }
            override fun onCreate() {
                super.onCreate()
                Log.i(TAG, "onCreate")
            }
        
            override fun onBind(intent: Intent?): IBinder {
                Log.i(TAG, "onBind")
                return bind
            }
        
        
            override fun onDestroy() {
                super.onDestroy()
                Log.i(TAG, "onDestroy")
            }
        
            override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                Log.i(TAG, "onStartCommand")
                // 防止服务被系统杀死后自动重启
                return START_NOT_STICKY
            }
        
            private val bind = object : IAIDLInterface.Stub() {
                @Throws(RemoteException::class)
                override fun register(callback: IAIDLCallback?) {
                    Log.i(TAG, "register IDemoAidlCallback")
                    callback?.let { callbackList.register(it) }
                    aidlStatus(true)
                }
        
                @Throws(RemoteException::class)
                override fun unregister(callback: IAIDLCallback?) {
                    Log.i(TAG, "unregister IDemoAidlCallback")
                    callback?.let { callbackList.unregister(it) }
                    aidlStatus(false)
                }
        
                @Throws(RemoteException::class)
                override fun aidlStatus(isConnected: Boolean) {
                    Log.i(TAG, "aidlStatus isConnected:$isConnected")
                    val size = callbackList.beginBroadcast()
                    for (i in 0 until size) {
                        callbackList.getBroadcastItem(i).aidlStatus(isConnected)
                    }
                    callbackList.finishBroadcast()
                }
        
                @Throws(RemoteException::class)
                override fun stringChanged(string: String?) {
                    Log.i(TAG, "stringChanged string:$string")
                    val size = callbackList.beginBroadcast()
                    for (i in 0 until size) {
                        callbackList.getBroadcastItem(i).aidlChanged(string)
                    }
                    callbackList.finishBroadcast()
                }
        
                @Throws(RemoteException::class)
                override fun itemChanged(item: ItemBean?) {
                    Log.i(TAG, "itemChanged item:$item")
                    val size = callbackList.beginBroadcast()
                    for (i in 0 until size) {
                        callbackList.getBroadcastItem(i).itemChanged(item)
                    }
                    callbackList.finishBroadcast()
                }
        
            }
        }
   ```
###### 2.2 客服端实现对服务端的连接

   ```kotlin
    package com.colin.android.demo.kotlin.client
    import android.content.ComponentName
    import android.content.Context
    import android.content.Intent
    import android.content.ServiceConnection
    import android.os.IBinder
    import com.colin.android.demo.kotlin.IAIDLCallback
    import com.colin.android.demo.kotlin.IAIDLInterface
    import com.colin.android.demo.kotlin.ItemBean
    import com.colin.android.demo.kotlin.service.AIDLService
    
      class AIDLClient(
      private val context: Context, private val callback: Callback
      ) {
    
      private var aidlService: IAIDLInterface? = null
          
        private val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                aidlService = IAIDLInterface.Stub.asInterface(service)
                aidlService?.register(aidlCallback)
            }
    
            override fun onServiceDisconnected(name: ComponentName?) {
                aidlService?.unregister(aidlCallback)
                aidlService = null
            }
        }
    
        fun bindService(`package`: String = "com.colin.android.demo.kotlin") {
            val intent = Intent(AIDLService.ACTION).apply {
                this.`package` = `package`
            }
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    
        fun unbindService() {
            context.unbindService(connection)
        }
    
        private val aidlCallback = object : IAIDLCallback.Stub() {
            override fun aidlStatus(isConnected: Boolean) {
                callback.aidlStatus(isConnected)
            }
    
            override fun aidlChanged(data: String?) {
                callback.aidlChanged(data)
            }
    
            override fun itemChanged(itembean: ItemBean?) {
                callback.itemChanged(itembean)
            }
        }
    
        interface Callback {
            fun aidlStatus(isConnected: Boolean)
            fun aidlChanged(data: String?)
            fun itemChanged(itemBean: ItemBean?)
        }
          
    }
   ```
   
###### 2.3 实现服务端和客服端需要传输的自定义数据结构（可选）

  ```kotlin
    package com.colin.android.demo.kotlin
    import android.os.Parcelable
    import kotlinx.parcelize.Parcelize
    
    @Parcelize
    data class ItemBean(val id: Int, val title: String) : Parcelable
  ```

##### 3. **其他**：

###### 3.1 服务端实现
1. 配置清单需 android:exported="true"
2. 需要考虑服务端启动，对AIDLService的启动次数
  ```xml
    <service
        android:name="com.colin.android.demo.kotlin.service.AIDLService"
        android:enabled="true"
        android:exported="true"
        android:process=":remote">
        <intent-filter>
            <action android:name="com.colin.android.demo.kotlin.service.AIDLService" />
        </intent-filter>
    </service>
  
   ```
###### 3.2 服务端实现
配置清单需 queries package，服务端所在的包名
  ```xml
        <queries>
            <package android:name="com.colin.android.demo.kotlin" />
        </queries>
  
   ```
