package com.example.before;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewTestActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondwebview);
        WebView xiaojia = (WebView) findViewById(R.id.xiaojia);

        xiaojia.loadUrl("file:///android_asset/444.html");
        xiaojia.getSettings().setJavaScriptEnabled(true);
        xiaojia.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        xiaojia.getSettings().setLoadWithOverviewMode(false);
        xiaojia.getSettings().setBuiltInZoomControls(true);
        xiaojia.getSettings().setSupportZoom(true);
        xiaojia.getSettings().setUseWideViewPort(true);
        xiaojia.addJavascriptInterface(new AndroidtoJs(), "test");
        xiaojia.getSettings().setDefaultTextEncodingName("utf-8");
    }
}
