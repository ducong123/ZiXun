package com.nainaiwang.zixun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class CommentListActivity extends Activity  implements View.OnClickListener {
    private RelativeLayout back;
    private TextView submitBtn;
    private EditText commentContent;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentlist);
        initView();
        initData();
    }

    private void initView() {
        back = (RelativeLayout)findViewById(R.id.relativelayout_commentilst_back);
        commentContent =(EditText)findViewById(R.id.edittext_commentcontent);
        submitBtn = (TextView)findViewById(R.id.textview_submit_but);
        back.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

    }
    private void initData() {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativelayout_commentilst_back:
                finish();
                break;
            case R.id.textview_submit_but:
              String text =  commentContent.getText().toString();
                Intent intent = getIntent();
                id = intent.getStringExtra("id");
                comment(id,text);
                break;

        }
    }
    private void comment(final String article_id, String text) {
        // TODO Auto-generated method stub
        RequestParams commentParams = new RequestParams(UrlUtils.COMMENTlIST);
        commentParams.addBodyParameter("article_id", article_id);//此处传入值要与接口传入值一致
        commentParams.addBodyParameter("text", text);
        x.http().post(commentParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
                System.out.println("arg0 = " + arg0 +"信息");
                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    String success = jsonObject.getString("success");
                    String info = jsonObject.getString("info");

                    if ("0".equals(success)) {
                        Toast.makeText(CommentListActivity.this,info,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommentListActivity.this,"评论成功",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
            }


        });
    }
}
