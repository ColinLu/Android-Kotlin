// ICallbackFromWebProcessInterface.aidl
package com.colin.library.android.widget.web;

// Declare any non-default types here with import statements

interface ICallbackFromWebProcessInterface {
    void onResult(String callbackname, String response);
}