package com.nainaiwang.zixun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 *
 * @author John
 * @Time 2016-4-13
 *
 */
public class MyCenterActivity extends Activity implements OnClickListener {

	private TextView checkLoginBtn;
	private Editor editor;
	private SharedPreferences sp;
	private ProgressDialog progressDialog;// 进度框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycenter);

		initView();// 初始化控件
		initData();// 初始化数据

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sp = getSharedPreferences("nainaiwang", MODE_PRIVATE);
		editor = sp.edit();
		onCenter();
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void initView() {
		// TODO Auto-generated method stub

		checkLoginBtn = (TextView) findViewById(R.id.checklogin);

		checkLoginBtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		//个人中心
		//个人中心
	}

	public void  onCenter(){
		// TODO Auto-generated method stub
		RequestParams loginParams = new RequestParams(UrlUtils.CHECKLOG);
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
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					String log = jsonObject.getString("log");

					if ("0".equals(log)) {
						Toast.makeText(MyCenterActivity.this, "未登录",
								Toast.LENGTH_SHORT).show();
						Intent loginToForgetpassword = new Intent(MyCenterActivity.this,
								LoginActivity.class);
						startActivity(loginToForgetpassword);

					} /*else if("1".equals(log)) {
						Toast.makeText(MyCenterActivity.this,
								"已登录",
								Toast.LENGTH_SHORT).show();

					}*/
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

}
