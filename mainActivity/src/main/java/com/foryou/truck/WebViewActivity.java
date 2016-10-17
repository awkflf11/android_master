package com.foryou.truck;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.foryou.truck.util.BindView;

public class WebViewActivity extends BaseActivity {

	@BindView(id = R.id.webview)
	private WebView mWebView;
	public String url;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.webview);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = getIntent().getStringExtra("url");
		initWebView();
		mWebView.loadUrl(url);
		ShowBackView();
		setTitle("福佑卡车");
	}

	private void initWebView() {
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);

		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		String dbPath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(dbPath);

		settings.setAppCacheEnabled(true);
		String appCaceDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(appCaceDir);

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				showProgressDialog();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				cancelProgressDialog();
			}

		});

	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub

	}
}