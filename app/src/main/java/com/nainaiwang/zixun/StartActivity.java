package com.nainaiwang.zixun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

public class StartActivity extends Activity implements View.OnClickListener {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Boolean isFirst;
    private MyThread m;

    private RelativeLayout adrl;
    private ImageView ad;//广告
    private TextView skip;

    private int time = 5;
    private boolean flag = false;
    private boolean isSkip = false;

    private ImageOptions options;

    private String url;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            time = time - msg.arg1;

            if (flag) {
                skip.setText(time + "  跳过");
            }

            if (time == 0) {
                if (isFirst) {
                    Intent qishiToYindao = new Intent(StartActivity.this,
                            MainActivity.class);//以后如果加引导页的话，可以直接在这里替换掉
                    startActivity(qishiToYindao);
                    isFirst = false;
                    editor.putBoolean("isFirst", false);
                    editor.commit();
                    finish();
                } else {
                    Intent qishiToMain = new Intent(StartActivity.this,
                            MainActivity.class);
                    startActivity(qishiToMain);
                    finish();
                }
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initView();

        initData();


    }

    private void initView() {
        adrl = (RelativeLayout) findViewById(R.id.relativelayout_start_adrl);
        skip = (TextView) findViewById(R.id.textview_start_skip);
        ad = (ImageView) findViewById(R.id.imageview_start_ad);

        ad.setOnClickListener(this);
        skip.setOnClickListener(this);
    }

    private void initData() {
        // TODO Auto-generated method stub

        downAd();

        options = new ImageOptions.Builder().setIgnoreGif(true)
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.jiazai)
                .setFailureDrawableId(R.mipmap.jiazai)
                .setImageScaleType(ImageView.ScaleType.FIT_XY).build();

        sp = getSharedPreferences("nainaiwang", MODE_PRIVATE);
        editor = sp.edit();
        isFirst = sp.getBoolean("isFirst", true);

        m = new MyThread();
        new Thread(m).start();

    }

    private void downAd() {
        RequestParams adParams = new RequestParams(UrlUtils.AD);
        x.http().post(adParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                flag = true;
                adrl.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String pic = jsonObject.getString("img");
                    url = jsonObject.getString("url");
                    x.image().bind(ad, pic, options);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_start_ad:
                //轮播图的点击事件
                Intent mainToDetails = new Intent(StartActivity.this, DetailsActivity.class);
                mainToDetails.putExtra("id", "广告");
                mainToDetails.putExtra("url", url);
                startActivity(mainToDetails);
                finish();
                break;
            case R.id.textview_start_skip:
                Toast.makeText(StartActivity.this, "跳过", Toast.LENGTH_SHORT).show();
                isSkip = true;
                if (isFirst) {
                    Intent qishiToYindao = new Intent(StartActivity.this,
                            MainActivity.class);//以后如果加引导页的话，可以直接在这里替换掉
                    startActivity(qishiToYindao);
                    isFirst = false;
                    editor.putBoolean("isFirst", false);
                    editor.commit();
                    finish();
                } else {
                    Intent qishiToMain = new Intent(StartActivity.this,
                            MainActivity.class);
                    startActivity(qishiToMain);
                    finish();
                }
                break;
        }
    }


    class MyThread implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for (int i = 5; i >= 0; i--) {
                if (isSkip) {
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }

            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSkip = true;
    }
}
