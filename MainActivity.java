package com.stealth.browser;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36");
        settings.setSavePassword(false);
        settings.setSaveFormData(false);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                String disableWebRTC = "javascript:(function() {" +
                        "window.RTCPeerConnection = null;" +
                        "window.webkitRTCPeerConnection = null;" +
                        "window.mozRTCPeerConnection = null;" +
                        "if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {" +
                        "    navigator.mediaDevices.getUserMedia = null;" +
                        "}" +
                        "})();";
                view.loadUrl(disableWebRTC);

                String spoofCanvas = "javascript:(function() {" +
                        "const originalGetContext = HTMLCanvasElement.prototype.getContext;" +
                        "HTMLCanvasElement.prototype.getContext = function(type, ...args) {" +
                        "    const context = originalGetContext.call(this, type, ...args);" +
                        "    if (type === '2d') {" +
                        "        const originalFillText = context.fillText;" +
                        "        context.fillText = function(text, x, y) {" +
                        "            return originalFillText.call(this, text, x + 0.1, y + 0.1);" +
                        "        };" +
                        "    }" +
                        "    return context;" +
                        "};" +
                        "})();";
                view.loadUrl(spoofCanvas);

                String hideFonts = "javascript:(function() {" +
                        "Object.defineProperty(document, 'fonts', {" +
                        "    get: function() { return undefined; }" +
                        "});" +
                        "})();";
                view.loadUrl(hideFonts);
                
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.loadUrl("https://freecash.com");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
