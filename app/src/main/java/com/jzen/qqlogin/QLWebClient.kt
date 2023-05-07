package com.jzen.qqlogin

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.MutableLiveData

class QLWebClient : WebViewClient() {
    companion object {
        private const val TAG = "QLWebClient"
    }

    // 用于显示到EditView
    val webViewUrl: MutableLiveData<String?> = MutableLiveData()

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest?
    ): Boolean {
        Log.d(TAG, "shouldInterceptRequest:${request?.url}")

        val reqUrl = request?.url.toString()
        // 默认Http的scheme
        if (reqUrl.startsWith("http")){
            return super.shouldOverrideUrlLoading(view, request)
        }

        try {
            // 特殊scheme
            // 替换一些字符串，不然可能会跳转到chrome浏览器
            val newUrl = reqUrl.replace("googlechrome", "qqlogin")
                .replace("Chrome", "QQLogin")
            // 长这样的
            //wtloginmqq://ptlogin/qlogin?p=https%3A%2F%2Fssl.ptlogin2.qq.com%2Fjump%3Fu1%3Dhttps%253A%252F%252Fconnect.qq.com%26pt_report%3D1%26pt_aid%3D716027609%26daid%3D383%26style%3D35%26pt_ua%3D888504C5A5413B9263C41B3101C9E89F%26pt_browser%3DChrome%26pt_3rd_aid%3D100497308%26pt_openlogin_data%3Dappid%253D716027609%2526pt_3rd_aid%253D100497308%2526daid%253D383%2526pt_skey_valid%253D0%2526style%253D35%2526s_url%253Dhttps%25253A%25252F%25252Fconnect.qq.com%2526refer_cgi%253Dauthorize%2526which%253D%2526sdkp%253Dpcweb%2526sdkv%253Dv1.0%2526time%253D1683451406%2526loginty%253D3%2526h5sig%253DdVdtss6vzI8hsnMUD5JPktCNB6gkwfWwnBaxwap5e90%2526response_type%253Dcode%2526client_id%253D100497308%2526redirect_uri%253Dhttps%25253A%25252F%25252Fy.qq.com%25252Fm%25252Flogin%25252Fredirect.html%25253Fis_qq_connect%25253D1%252526login_type%25253D1%252526surl%25253Dhttps%2525253A%2525252F%2525252Fi.y.qq.com%2525252Fn2%2525252Fm%2525252Fshare%2525252Fdetails%2525252Ftaoge.html%2525253FADTAG%2525253Dmyqq%25252526from%2525253Dmyqq%25252526channel%2525253D10007100%25252526id%2525253D7256912512%2526state%253Dstate%2526display%253Dmobile%2526pt_flex%253D1%2526loginfrom%253D%2526h5sig%253DdVdtss6vzI8hsnMUD5JPktCNB6gkwfWwnBaxwap5e90%2526loginty%253D3%2526&schemacallback=googlechrome%3A%2F%2F
            Log.d(TAG, newUrl)
            try {
                // 调起手机QQ
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
                view.context.startActivity(intent)
                // ！！！【需要将此应用设置为默认浏览器，否则手机QQ不会回调回来】！！！
            } catch (e: Exception) {
                // 没有安装
                e.printStackTrace()
                Log.d(TAG, "QQ未安装")
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d(TAG, "onPageFinished:$url")
        webViewUrl.value = url

        // 每次打开页面都显示一下cookie
        CookieManager.getInstance().apply {
            Log.e(TAG, getCookie(url))
        }
    }
}