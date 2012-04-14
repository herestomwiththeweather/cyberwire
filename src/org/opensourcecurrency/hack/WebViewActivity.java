package org.opensourcecurrency.hack;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class WebViewActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				setProgress(progress * 100);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			private static final String TAG = "OpenTransact";

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setTitle(url);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d(TAG,"onPageFinished: " + url);

				Uri uri = Uri.parse(url);
				String code = uri.getQueryParameter("code");
				if (code != null) {
				  Intent result = new Intent();
				  result.putExtra("token", code);
				  setResult(RESULT_OK, result);
				  finish();
				}
			}
		});
		setContentView(webview);
		
		Intent intent = getIntent();
		if (intent.getData() != null) {
			webview.loadUrl(intent.getDataString());
		}
	}

}