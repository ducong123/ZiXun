package com.nainaiwang.zixun;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.bean.Details;
import com.nainaiwang.utils.UrlUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends Activity implements View.OnClickListener {

    private TextView name, author, time;
    private RelativeLayout back;
    private WebView webView;
    private String id;

    private List<Details> detailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initView();
        initData();

    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webview_details_webview);
        back = (RelativeLayout) findViewById(R.id.relativelayout_details_back);

        back.setOnClickListener(this);

    }

    private void initData() {

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        if ("广告".equals(id)) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().getBuiltInZoomControls();
            webView.setVerticalScrollBarEnabled(false);
            webView.getSettings().setBlockNetworkImage(false);
            webView.loadUrl(intent.getStringExtra("url"));

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        } else {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().getBuiltInZoomControls();
            webView.setVerticalScrollBarEnabled(false);
            webView.getSettings().setBlockNetworkImage(false);
            webView.loadUrl(UrlUtils.DETAILS + id);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativelayout_details_back:
                if ("广告".equals(id)) {
                    Intent in = new Intent(DetailsActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ("广告".equals(id)) {
            Intent i = new Intent(DetailsActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            finish();
        }
        return true;
    }
}
