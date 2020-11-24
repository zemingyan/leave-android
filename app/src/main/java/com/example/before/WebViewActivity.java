package com.example.before;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstwebview);
        WebView wv_produce = (WebView) findViewById(R.id.vw_product);
        wv_produce.loadUrl("file:///android_asset/1.html");
        wv_produce.getSettings().setJavaScriptEnabled(true);
        wv_produce.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        wv_produce.getSettings().setLoadWithOverviewMode(true);
        wv_produce.getSettings().setBuiltInZoomControls(true);
        wv_produce.getSettings().setSupportZoom(true);
        wv_produce.getSettings().setUseWideViewPort(true);
        wv_produce.addJavascriptInterface(new AndroidtoJs(), "test");
        wv_produce.getSettings().setDefaultTextEncodingName("utf-8");
    }
}
