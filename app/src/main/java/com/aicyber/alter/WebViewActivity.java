
package com.aicyber.alter;


import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends BaseActivity {
	private WebView mWebView;     //webView �ؼ�
	WebSettings webSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_webview);
		
		/*
		mWebView = (WebView)this.findViewById(R.id.webview);
		webSettings = mWebView .getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.requestFocusFromTouch();
		mWebView.loadUrl("http://v.baidu.com/");*/
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
