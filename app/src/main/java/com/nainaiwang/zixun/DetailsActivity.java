package com.nainaiwang.zixun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.bean.Details;
import com.nainaiwang.utils.UrlUtils;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends Activity implements View.OnClickListener {

    private TextView name, author, time;
    private RelativeLayout back;
    private WebView webView;
    private String id;
    private SharedPreferences.Editor editor;
    private boolean isColl;//判断是否收藏
    private boolean isLog;//判断是否登录
    private SharedPreferences sp;
    private List<Details> detailsList = new ArrayList<>();

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sp = getSharedPreferences("nainaiwang", MODE_PRIVATE);
        editor = sp.edit();
       isLog = sp.getBoolean("isLog",false);
        System.out.println(isLog);
        webView.getSettings().setJavaScriptEnabled(true);//启用javaScript调用功能
        webView.loadUrl(UrlUtils.DETAILS + id);
       /* if("1".equals(success)){
            webView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView webView,String url){
                    System.out.println("4");
                    webView.loadUrl("javascript:yesColl()");//执行js
                }
            });
        }*/

    }
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
        System.out.println("Intent"+id);
        if ("广告".equals(id)) {
            System.out.println("dd");
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

            webView.getSettings().setJavaScriptEnabled(true);//启用javaScript调用功能
            webView.getSettings().getBuiltInZoomControls();
            webView.setVerticalScrollBarEnabled(false);
            webView.getSettings().setBlockNetworkImage(false);
            webView.loadUrl(UrlUtils.DETAILS + id);

            webView.addJavascriptInterface(this,"s");
            System.out.println("tt");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
              /*  public void onPageFinished(WebView webView,String url){
                    webView.loadUrl("javascript:yesColl()");//执行js
                }*/
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
    public boolean onKeyDown(int keyCode, KeyEvent event) { //设置回退  覆盖Activity类的onKeyDown()方法
        if ("广告".equals(id)) {
            Intent i = new Intent(DetailsActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            finish();
        }
        return true;
    }
        @JavascriptInterface
        public void collectionYes() {
            System.out.println("ss");
            RequestParams collecParams = new RequestParams(UrlUtils.COLLECTION);
            Intent intent = getIntent();
            id = intent.getStringExtra("id");
            String article_id=id;
            System.out.println("id="+id);
            collecParams.addBodyParameter("article_id",article_id);
            x.http().post(collecParams, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String arg0) {
                    System.out.println("收藏arg0"+arg0);
                    try {
                        JSONObject jsonObject = new JSONObject(arg0);
                        String success = jsonObject.getString("success");
                        String info = jsonObject.getString("info");
                        if("请登录后再操作".equals(info)){
                            System.out.println("3");
                            webView.getSettings().setJavaScriptEnabled(true);//启用javaScript调用功能
                            webView.loadUrl(UrlUtils.DETAILS + id);
                            webView.setWebViewClient(new WebViewClient() {

                                public void onPageFinished(WebView webView,String url){

                                    webView.loadUrl("javascript:noColl()");//执行js
                                }
                            });
                            Toast.makeText(DetailsActivity.this,info,Toast.LENGTH_SHORT).show();
                        }else{
                            if ("1".equals(success)) {
                                Toast.makeText(DetailsActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                                editor.putString("success",success);
                                editor.commit();
                                System.out.println("11");

                            } else {
                                Toast.makeText(DetailsActivity.this,info,Toast.LENGTH_SHORT).show();
                                System.out.println("22");
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(Callback.CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        }
    @JavascriptInterface
    public void collectionNo() {
        System.out.println("取消");
        RequestParams collecNoParams = new RequestParams(UrlUtils.NOCOLLECTION);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String article_id=id;
        System.out.println("id="+id);
        collecNoParams.addBodyParameter("article_id",article_id);
        x.http().post(collecNoParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String arg0) {
                System.out.println("取消收藏arg0"+arg0);
                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    String success = jsonObject.getString("success");
                    String info = jsonObject.getString("info");
                if("请登录后再操作".equals(info)){
                    webView.getSettings().setJavaScriptEnabled(true);//启用javaScript调用功能
                    webView.loadUrl(UrlUtils.DETAILS + id);
                    webView.setWebViewClient(new WebViewClient() {

                        public void onPageFinished(WebView webView,String url){

                            webView.loadUrl("javascript:noColl()");//执行js
                        }
                    });
                }else {
                    if ("1".equals(success)) {
                        Toast.makeText(DetailsActivity.this,"取消成功",Toast.LENGTH_SHORT).show();
                        System.out.println("取消");
                    } else {
                        Toast.makeText(DetailsActivity.this,info,Toast.LENGTH_SHORT).show();
                        System.out.println("取消失败");
                    }
                }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

   @JavascriptInterface
   public  void commentListTo(){
       Intent intent = getIntent();
       id = intent.getStringExtra("id");
       Intent commentListTo = new Intent(DetailsActivity.this,
               CommentListActivity.class);
       commentListTo.putExtra("id",id);
       startActivity(commentListTo);// 跳转到注册页面
       finish();// 退出当前页面

   }
}

