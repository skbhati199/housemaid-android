package com.housemaid.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.housemaid.R;
import com.housemaid.databinding.ActivityPaymentGatwayBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentGatwayActivity extends BaseActivity {

    ActivityPaymentGatwayBinding binding;
    SharedPreference sharedPreference;
    SignUpModel paymentGatewayInformation;
    ProgressDialog pd;
    int offer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_gatway);
        init();
        initControls();

    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        String accessToken = sharedPreference.getString("signUp_token", "");
        offer_id = getIntent().getIntExtra("offerid", 0);

        if (offer_id != 0) {
            getPaymentGateway(accessToken, offer_id);
        } else binding.progress.setVisibility(View.GONE);

        binding.errorMsg.setVisibility(View.GONE);
    }

    @Override
    public void initControls() {
        super.initControls();
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            finish();
        }
    }

    private void getPaymentGateway(String access_token, int offer_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getpaymentGateway(access_token, offer_id);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    paymentGatewayInformation = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {
                        String url = paymentGatewayInformation.getPayment_url();

                        if (!url.isEmpty()) {
//                            binding.progress.setVisibility(View.GONE);
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
                    } else {
                        Toast.makeText(PaymentGatwayActivity.this, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(PaymentGatwayActivity.this, SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {

                            ErrorResponse messageObj = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                            /*Toast.makeText(PaymentGatwayActivity.this, "" + response
                                    .errorBody().string(), Toast.LENGTH_SHORT).show();*/

                            //Toast.makeText(PaymentGatwayActivity.this, "" + messageObj.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.errorMsg.setText(messageObj.getMessage());
                            binding.errorMsg.setVisibility(View.VISIBLE);
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(PaymentGatwayActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            pd = new ProgressDialog(PaymentGatwayActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
            if (url.equals("http://13.58.98.218/Maid/api/buyNow")) {

            }

            if (url.equals("http://13.58.98.218/Maid/api/paymentcredit-result")) {
                showPopUp();
                pd.cancel();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pd.cancel();
            binding.progress.setVisibility(View.GONE);

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            pd.cancel();
            binding.progress.setVisibility(View.GONE);
        }
    }

    public void showPopUp() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_listing_done);
        TextView tvLine = dialog.findViewById(R.id.tvP);
        tvLine.setText(R.string.payment_done_successfully);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    startActivity(new Intent(PaymentGatwayActivity.this,
                            UpgradeMemberShipActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 2000);
    }
}