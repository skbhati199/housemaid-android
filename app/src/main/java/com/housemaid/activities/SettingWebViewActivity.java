package com.housemaid.activities;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.housemaid.R;
import com.housemaid.databinding.ActivitySettingWebViewBinding;
import com.housemaid.utils.SharedPreference;

public class SettingWebViewActivity extends BaseActivity implements View.OnClickListener {

    ActivitySettingWebViewBinding binding;
    String keyForPage;
    String url;
    SharedPreference sharedPreference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_web_view);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        keyForPage = getIntent().getStringExtra("key_for_page");

        if (keyForPage.equals("about")) {
            url = "http://13.58.98.218/Maid/about";
        }
        if (keyForPage.equals("terms")) {
            url = "http://13.58.98.218/Maid/term-condition";
        }
        if (keyForPage.equals("help")) {
            String userId = sharedPreference.getString("user_id","");
            //url = "http://13.58.98.218/Maid/help-to-develop";
            url = "http://13.58.98.218/Maid/contact-form/"+userId;
        }
        if (keyForPage.equals("contact")) {
            url = "http://13.58.98.218/Maid/contact";
        }
        pd = new ProgressDialog(SettingWebViewActivity.this);
        pd.setMessage("Please Wait");
        pd.setCancelable(false);
        pd.show();
        setWebView();
    }

    private void setWebView() {
        if (!url.isEmpty()) {
//          binding.progress.setVisibility(View.GONE);
            binding.webView.setInitialScale(1);
            binding.webView.getSettings().setLoadWithOverviewMode(true);
            binding.webView.getSettings().setUseWideViewPort(true);
            binding.webView.getSettings().setLoadsImagesAutomatically(true);
            binding.webView.getSettings().setJavaScriptEnabled(true);
            binding.webView.getSettings().setBuiltInZoomControls(true);
            binding.webView.getSettings().setSupportZoom(true);
            binding.webView.setWebViewClient(new MyBrowser());
            binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            binding.webView.loadUrl(url);
        }
    }

    @Override
    public void initControls() {
        super.initControls();
    }

    @Override
    public void onClick(View v) {

    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pd.cancel();

        }
    }
}
