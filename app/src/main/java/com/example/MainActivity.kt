package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AACViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AACViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ReactAACContainer(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

class AndroidAACBridge(private val activity: ComponentActivity, private val viewModel: AACViewModel) {
    @JavascriptInterface
    fun speakText(text: String, pitch: Float, rate: Float) {
        activity.runOnUiThread {
            viewModel.speakSentence(text)
        }
    }

    @JavascriptInterface
    fun showToast(msg: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun onEmotionDetected(emotion: String) {
        activity.runOnUiThread {
            viewModel.setManualEmotion(emotion)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ReactAACContainer(
    viewModel: AACViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    mediaPlaybackRequiresUserGesture = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }
                webViewClient = WebViewClient()
                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest) {
                        // Grant camera and audio capture permissions to the React web app
                        request.grant(request.resources)
                    }
                }
                if (activity != null) {
                    addJavascriptInterface(AndroidAACBridge(activity, viewModel), "AndroidAACBridge")
                }
                loadUrl("file:///android_asset/web/index.html")
            }
        },
        modifier = modifier
    )
}
