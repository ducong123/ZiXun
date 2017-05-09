package com.nainaiwang.zixun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.myreceiver.JudgeNetIsConnectedReceiver;
import com.nainaiwang.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 *
 * @author John
 * @Time 2016-4-13
 *
 */
public class SetPasswordActivity extends Activity implements OnClickListener {

	private RelativeLayout back;//返回按钮
	private EditText inputOldPassword,inputNewPassword,inputAgainPassword;
	private TextView save;
	private SharedPreferences sp;//存储数据
	private String token;

	private ProgressDialog progressDialog;// 进度框
	private JudgeNetIsConnectedReceiver receiver;//广播接受者
	private boolean isNet;//是否有网络
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_pwd);

		receiver = new JudgeNetIsConnectedReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver,intentFilter);
		sp = getSharedPreferences("nainaiwang",MODE_PRIVATE);

        token = sp.getString("token",null);

		initView();// 初始化控件
		initData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sp = getSharedPreferences("nainaiwang", MODE_PRIVATE);
	}
	private void initData() {
		// TODO Auto-generated method stub

	}
	private void initView() {
		// TODO Auto-generated method stub
	    back = (RelativeLayout)findViewById(R.id.relativelayout_setpassword_back);
		inputOldPassword = (EditText)findViewById(R.id.edittext_oldpass);
		inputNewPassword = (EditText)findViewById(R.id.edittext_newpass);
		inputAgainPassword = (EditText)findViewById(R.id.edittext_againpass);
		save =(TextView)findViewById(R.id.textview_save);

		back.setOnClickListener(this);
		save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		//个人中心
		int id = v.getId();
		switch (id) {
			case R.id.relativelayout_setpassword_back:
				finish();
				break;
			case R.id.textview_save:
				String str1 =inputOldPassword.getText().toString().trim();
				String str2 =inputNewPassword.getText().toString().trim();
				String str3 =inputAgainPassword.getText().toString().trim();
				if(TextUtils.isEmpty(str1)){
					Toast.makeText(SetPasswordActivity.this,"请输入当前密码",Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(str2)){
					Toast.makeText(SetPasswordActivity.this,"请输入新密码",Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(str3)){
					Toast.makeText(SetPasswordActivity.this,"请输入确认新密码",Toast.LENGTH_SHORT).show();
				}else if(!str2.equals(str3)){
					Toast.makeText(SetPasswordActivity.this,"新密码和确认密码不匹配，请重新输入",Toast.LENGTH_SHORT).show();
					inputNewPassword.setText("");
					inputAgainPassword.setText("");
				}else{
					isNet = receiver.judgeNetIsConnected(SetPasswordActivity.this);
					if(isNet){
						save(str1,str2,str3);//保存密码并上传
					}else {
						Toast.makeText(SetPasswordActivity.this,"当前没有网络",Toast.LENGTH_SHORT).show();
					}
				}
				break;
			default:
				break;
		}
	}

	/*
	* 保存密码并上传修改密码*/
	protected void  save(String str1,String str2,String str3){
		System.out.println("token="+ token + ",str1=" +str1+",str2="+str2+",str3="+str3);//打印值

		progressDialog = new ProgressDialog(SetPasswordActivity.this);
		progressDialog.setMessage("正在加载，请稍候...");
		progressDialog.show();//显示加载动画
        RequestParams saveParams = new RequestParams(UrlUtils.SET_PASSWORD);//调用修改密码接口
        saveParams.addBodyParameter("token",token);
        saveParams.addBodyParameter("old_pass",str1);
        saveParams.addBodyParameter("new_repass",str3);
        saveParams.addBodyParameter("new_pass",str2);
        x.http().post(saveParams, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = new JSONObject(arg0);
                    String success = jsonObject1.getString("success");
                    String info = jsonObject1.getString("info");
                    if ("1".equals(success)) {
                        progressDialog.dismiss();
                        System.out.println("成功");
                        Toast.makeText(SetPasswordActivity.this, "修改成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if("0".equals(success)){
                        Toast.makeText(SetPasswordActivity.this, info,
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

}
