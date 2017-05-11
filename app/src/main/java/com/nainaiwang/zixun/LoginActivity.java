package com.nainaiwang.zixun;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.nainaiwang.utils.UrlUtils;
import com.nainaiwang.utils.Validator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author John
 * @Time 2016-4-13
 *
 */
public class LoginActivity extends Activity implements OnClickListener {

	private TextView forgetPassword, loginBtn, registerBtn;// 返回，忘记密码，注册，登录
	private EditText inputAccount, inputPassword;// 输入账号密码框
	private RelativeLayout back;// 返回

	private SharedPreferences sp;
	private Editor editor;
	private ProgressDialog progressDialog;// 进度框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initView();// 初始化控件
		initData();// 初始化数据

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sp = getSharedPreferences("nainaiwang", MODE_PRIVATE);
		editor = sp.edit();
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (RelativeLayout) findViewById(R.id.relativelayout_login_back);
		forgetPassword = (TextView) findViewById(R.id.textview_login_forgetpassword);
		loginBtn = (TextView) findViewById(R.id.textview_login_login);
		registerBtn = (TextView) findViewById(R.id.textview_login_freeresiger);
		inputAccount = (EditText) findViewById(R.id.edittext_login_inputaccount);
		inputPassword = (EditText) findViewById(R.id.edittext_login_inputpassword);

		back.setOnClickListener(this);
		forgetPassword.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
			case R.id.relativelayout_login_back:
				finish();// 退出登录页面
				break;
			/*case R.id.textview_login_forgetpassword:
				Intent loginToForgetpassword = new Intent(LoginActivity.this,
						FindPasswordActivity.class);
				startActivity(loginToForgetpassword);// 跳转到“找回密码”页面
				break;*/ /*忘记密码跳转页面*/
			case R.id.textview_login_login:
				String account = inputAccount.getText().toString().trim();
				String password = inputPassword.getText().toString().trim();
				if (TextUtils.isEmpty(account)) {
					Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT)
							.show();
				} else if (TextUtils.isEmpty(password)) {
					Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT)
							.show();
				} else {
					login(account, password);// 登录
				}
				break;
			case R.id.textview_login_freeresiger:
				Intent regToRegister = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(regToRegister);// 跳转到注册页面
				finish();// 退出当前页面
				break;

			default:
				break;
		}
	}

	/**
	 * 登录
	 *
	 * @param account
	 * @param password
	 */
	private void login(final String account, String password) {
		// TODO Auto-generated method stub
		RequestParams loginParams = new RequestParams(UrlUtils.LOGIN);
		loginParams.addBodyParameter("account", account);//此处传入值要与接口传入值一致
		loginParams.addBodyParameter("password", password);
		x.http().post(loginParams, new CommonCallback<String>() {
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

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				System.out.println("arg0 = " + arg0 +"信息");
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					String errorCode = jsonObject.getString("errorCode");
					String info = jsonObject.getString("info");
					if ("0".equals(errorCode)) {
						editor.commit();
						Toast.makeText(LoginActivity.this, "登录成功",
								Toast.LENGTH_SHORT).show();
						finish();

					} else {
						Toast.makeText(LoginActivity.this, info,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
