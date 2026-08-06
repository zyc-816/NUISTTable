package com.example.nuisttable.ui.asset

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun FetchingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "提示") },
        text = { Text(text = "正在获取课表...") },
        confirmButton = { }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CreateWebView(url : String, onClose : () -> Unit, onLoginSuccess : (cookie : String, XH : String?) -> Unit) {
    var webView : WebView? by remember { mutableStateOf(null) }
    var hasReturned by remember { mutableStateOf(false) }
    BackHandler {
        if(webView?.canGoBack() == true) webView?.goBack()
        else onClose()
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
            webView = null
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView.setWebContentsDebuggingEnabled(true)
            WebView(context).apply {
                webView = this
                val cookieManager = CookieManager.getInstance()
                val desktopChromeUa = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, finUrl: String?) {
                        super.onPageFinished(view, finUrl)
                        if(hasReturned) return
                        finUrl ?: return
                        if(finUrl.endsWith("xskcb")) {
                            val cookie = cookieManager.getCookie("https://jwxt.nuist.edu.cn/jwapp/sys/wdkb/*default/index.do?EMAP_LANG=zh#/xskcb")
                            if(!cookie.isNullOrEmpty() && cookie.contains("GS_SESSIONID")) {
                                var XH: String? = ""
                                view?.evaluateJavascript("(function(){ return typeof userId !== 'undefined' ? userId : null; })();") { value ->
                                    XH = value?.removeSurrounding("\"")
//                                    Log.d("Data", "XH: $XH")
                                }
                                if(XH != null) {
                                    hasReturned = true
                                    onLoginSuccess(cookie, XH)
                                }
                            }
                        }
//                        Log.d("WebView", "finished: $finUrl")
                    }

//                    override fun onReceivedError(
//                        view: WebView?,
//                        request: WebResourceRequest?,
//                        error: WebResourceError?
//                    ) {
//                        super.onReceivedError(view, request, error)
//                        Log.e(
//                            "WebViewError",
//                            "url=${request?.url}, isMainFrame=${request?.isForMainFrame}, code=${error?.errorCode}, desc=${error?.description}"
//                        )
//                    }
//
//                    override fun onReceivedHttpError(
//                        view: WebView?,
//                        request: WebResourceRequest?,
//                        errorResponse: WebResourceResponse?
//                    ) {
//                        super.onReceivedHttpError(view, request, errorResponse)
//                        Log.e(
//                            "WebViewHttp",
//                            "url=${request?.url}, isMainFrame=${request?.isForMainFrame}, code=${errorResponse?.statusCode}, mime=${errorResponse?.mimeType}, reason=${errorResponse?.reasonPhrase}"
//                        )
//                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.userAgentString = desktopChromeUa
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportMultipleWindows(true)
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.setSupportZoom(true)
                loadUrl(url)
            }

        }
    )
}
