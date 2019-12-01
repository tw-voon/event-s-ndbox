package com.kuchingitsolution.asus.eventmanagement.feedback_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.WebViewClientImpl;

public class FeedbackFormActivity extends AppCompatActivity {

    private WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);

        setupWebView();
    }

    private void setupWebView() {
        browser = findViewById(R.id.webView);
        WebSettings setting = browser.getSettings();
        setting.setJavaScriptEnabled(true);

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        browser.setWebViewClient(webViewClient);

        browser.loadUrl(Config.DISPLAY_FEEDBACK_FORM);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.browser.canGoBack()) {
            this.browser.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
