package com.jzen.qqlogin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jzen.qqlogin.databinding.ActivityMainBinding

/**
 * 此项目用于研究使用WebView打开QQ音乐主页，然后调起手机QQ登录账号，登录成功后得到cookie。
 * 流程是可以走通的，最后也是成功拿到Cookie
 * 但是有个问题，就是调起手机QQ登录成功之后，手机QQ会调起默认浏览器（可以是Chrome）打开登录成功后的回调链接，而不是本应用。
 * 解决方案就是将本应用设置成默认浏览器。但是这种体验非常糟糕。
 * 我下载了许多种不同的浏览器，打开qq音乐网站，登录，调起手机QQ登录，回调打开的都是Chrome浏览器（非默认浏览器）。
 * 但是，我是用夸克浏览器调起手机QQ却可以返回到夸克，说明手机QQ可能对国内浏览器有特殊“照顾”。
 * 甚至Edge浏览器都没办法返回，手机QQ回调打开的依旧是Chrome。
 * 至此，研究结束。可行，但不可用。
 */

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webview.apply {
            webViewClient = QLWebClient().apply {
                webViewUrl.observe(this@MainActivity) {
                    binding.textUrl.setText(it)
                }
            }

            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                domStorageEnabled = true // 如果不启用Dom存储，qq音乐页面可能会显示空白
            }

            loadUrl("https://y.qq.com/")
        }

        binding.btnBack.setOnClickListener {
            binding.webview.goBack()
        }

        binding.btnForward.setOnClickListener {
            binding.webview.goForward()
        }

        binding.btnGo.setOnClickListener {
            binding.webview.loadUrl(binding.textUrl.text.toString())
        }

        // 被调起时，看看intent的内容是不是http(s)，如果是，那就跳转过去
        if(!intent.scheme.isNullOrEmpty()){
            if (intent.scheme!!.startsWith("http")){
                intent.data?.apply {
                    Log.d(Companion.TAG, "Full url: $this")
                    binding.webview.loadUrl(this.toString())
                }
            }
        }
    }
}