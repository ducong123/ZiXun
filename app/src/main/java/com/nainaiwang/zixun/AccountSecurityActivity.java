package com.nainaiwang.zixun;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 *
 * @author John
 * @Time 2016-4-13
 *
 */
public class AccountSecurityActivity extends Activity implements OnClickListener {

    private RelativeLayout back;//返回按钮
	private TextView setpasswordtvBut,setusernametvBut;//修改密码按钮,修改资料按钮
	private SharedPreferences sp;//存储数据
	private ProgressDialog progressDialog;// 进度框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		initView();// 初始化控件
		initData();// 初始化数据

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
        back = (RelativeLayout)findViewById(R.id.relativelayout_edit_back);
		setpasswordtvBut = (TextView)findViewById(R.id.textview_edit_setpasswordtv);//调用退出登录按钮

        back.setOnClickListener(this);
		setpasswordtvBut.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		//个人中心
		int id = v.getId();
		switch (id) {
            case R.id.relativelayout_edit_back:
                finish();
                break;
			case R.id.textview_edit_setpasswordtv:  //退出登录
				Intent setPassTo= new Intent(AccountSecurityActivity.this,
						SetPasswordActivity.class);
				startActivity(setPassTo);//跳转账户安全界面
				break;

			default:
				break;
		}
	}
}
