package com.aicyber.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NetWeb {

	private WebView mWebView = null;     //webView ¿Ø¼þ
	private WebSettings webSettings = null;
	//private Context mContext = null;
	@SuppressLint("SetJavaScriptEnabled")
	public WebView initWeb(WebView webView, String url, Context context)
	{
		mWebView = webView;
		webSettings = mWebView .getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.requestFocusFromTouch();
		//mWebView.loadUrl("http://v.baidu.com/");
		mWebView.loadUrl(url);
		
		
		return mWebView;
	}
} 
