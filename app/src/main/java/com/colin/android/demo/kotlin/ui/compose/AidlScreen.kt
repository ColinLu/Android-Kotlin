package com.colin.android.demo.kotlin.ui.compose

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.client.AIDLClient
import com.colin.android.demo.kotlin.service.AIDLService
import com.colin.android.demo.kotlin.ui.components.AppButton
import com.colin.android.demo.kotlin.ui.components.AppTopBar
import com.colin.android.demo.kotlin.ui.method.aidl.AidlServiceFragment
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AidlScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var sendIndex by remember { mutableIntStateOf(0) }
    
    val aidlClient = remember {
        AIDLClient(context, object : AIDLClient.Callback {
            override fun aidlStatus(isConnected: Boolean) {
                Log.i("aidlStatus $isConnected")
                ToastUtil.show("aidlStatus $isConnected")
            }
            override fun aidlChanged(data: String?) {
                Log.i("aidlChanged $data")
                ToastUtil.show("aidlChanged $data")
            }
            override fun itemChanged(itemBean: ItemBean?) {
                Log.i("itemChanged $itemBean")
                ToastUtil.show("itemChanged $itemBean")
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            aidlClient.unbindService()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "AIDL Test",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Service Section
            Text(
                text = "AIDL Service",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            AppButton(text = "Send Text") {
                val intent = Intent(context, AIDLService::class.java).apply {
                    action = AidlServiceFragment.ACTION_SEND_STRING
                    putExtra(AidlServiceFragment.KEY_SEND_VALUE, "sendText:$sendIndex")
                }
                context.startService(intent)
            }
            AppButton(text = "Send Object") {
                sendIndex += 1
                val intent = Intent(context, AIDLService::class.java).apply {
                    action = AidlServiceFragment.ACTION_SEND_ITEM
                    putExtra(AidlServiceFragment.KEY_SEND_VALUE, ItemBean(sendIndex, "sendIndex"))
                }
                context.startService(intent)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Client Section
            Text(
                text = "AIDL Client",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.banner1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 6f),
                contentScale = ContentScale.Crop
            )
            AppButton(text = "Status") {
                val status = aidlClient.isConnected()
                ToastUtil.show("isConnected $status")
            }
            AppButton(text = "Bind") {
                aidlClient.bindService()
            }
            AppButton(text = "Unbind") {
                aidlClient.unbindService()
            }
        }
    }
}

@ScreenPreviews
@Composable
fun AidlScreenPreview() {
    AppTheme {
        AidlScreen(onBackClick = {})
    }
}
