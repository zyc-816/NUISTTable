package com.example.nuisttable.ui.asset

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
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
fun CreateWebView(url : String, onClose : () -> Unit, onLoginSuccess : (cookie : String, XH : String) -> Unit) {
    var webView : WebView? by remember { mutableStateOf(null) }
    var hasReturned by remember { mutableStateOf(false) }
    var hasClosed by remember { mutableStateOf(false) }

    fun clearWebViewState(view: WebView?) {
        view ?: return
        view.stopLoading()
        view.clearHistory()
        view.clearFormData()
        view.clearCache(true)
    }

    fun closeWebView() {
        if (hasClosed) return
        hasClosed = true
        clearWebViewState(webView)
        onClose()
    }

    BackHandler {
        if(webView?.canGoBack() == true) webView?.goBack()
        else closeWebView()
    }

    DisposableEffect(Unit) {
        onDispose {
            clearWebViewState(webView)
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
                                view?.evaluateJavascript("(function(){ return typeof userId !== 'undefined' ? userId : null; })();") { value ->
                                    val xh = value
                                        ?.removeSurrounding("\"")
                                        ?.takeIf { it.isNotBlank() && it != "null" }
                                        ?: return@evaluateJavascript
                                    if (hasReturned) return@evaluateJavascript
                                    hasReturned = true
                                    onLoginSuccess(cookie, xh)
                                }
                            }
                        }
                    }
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
