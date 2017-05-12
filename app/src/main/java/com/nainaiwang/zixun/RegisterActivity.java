package com.nainaiwang.zixun;;

import junit.framework.Test;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.nainaiwang.myreceiver.JudgeNetIsConnectedReceiver;
import com.nainaiwang.utils.UrlUtils;
import com.nainaiwang.utils.Validator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {

    private TextView agreement, registerBtn, getVerifivationCodeTv;// 《用户注册协议》，注册,获取验证码里面的字
    private CheckBox iAgree;//是否有条款
    private RelativeLayout back, getVerificationCode, seePassword;// 返回，获取验证码，可视密码
    private EditText inputPhoneNum,inputVerification, inputUserName, inputPassword,inputAginPassword;// 输入手机号，输入验证码，输入密码,确认密码

    private boolean isSeePassword = false;// 点击眼睛，是否显示密码,默认不显示
    private boolean isAgreeAgreement=true;
    private boolean isNet;// 是否有网络

    private Handler handler = new Handler();
    private int reclen = 60;//倒计时60秒

    private SharedPreferences sp;
    private Editor editor;

    private ProgressDialog progressDialog;// 进度框
    private JudgeNetIsConnectedReceiver receiver;// 广播接受者

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

    /**
     * 初始化控件
     */
    private void initView() {
        // TODO Auto-generated method stub
        back = (RelativeLayout) findViewById(R.id.relativelayout_register_back);//返回控件
        seePassword = (RelativeLayout) findViewById(R.id.relativelayout_register_seepassword);//查看密码控件
        getVerificationCode = (RelativeLayout)findViewById(R.id.relativelayout_register_getverification);//验证码
        //agreement = (TextView)findViewById(R.id.textview_register_agressment);//用户注册协议
        registerBtn = (TextView) findViewById(R.id.textview_register_register);//注册按钮
        getVerifivationCodeTv = (TextView)findViewById(R.id.textview_register_getverificationtv);//验证码按钮内文字

        iAgree = (CheckBox)findViewById(R.id.checkbox_register_agressment);//选择是否同意协议

        inputVerification = (EditText)findViewById(R.id.edittext_register_verification);//输入验证码
        inputAginPassword = (EditText)findViewById(R.id.edittext_againpass);//再次输入密码
        inputPhoneNum = (EditText) findViewById(R.id.edittext_register_inputphone);//手机号输入框
        inputPassword = (EditText) findViewById(R.id.edittext_register_inputpassword);//密码输入框
        inputUserName = (EditText) findViewById(R.id.edittext_register_inputUsername);//用户名输入框

        back.setOnClickListener(this);//返回按钮的单击事件
        getVerificationCode.setOnClickListener(this);
        //agreement.setOnClickListener(this); //点击《用户注册协议》事件
        registerBtn.setOnClickListener(this);//注册按钮的单击事件
        seePassword.setOnClickListener(this);//查看密码的单击事件
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // TODO Auto-generated method stub

        receiver = new JudgeNetIsConnectedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);

        iAgree.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                isAgreeAgreement = arg1;
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.relativelayout_register_back:
                finish();
                break;
            case R.id.relativelayout_register_getverification:
                String phone = inputPhoneNum.getText().toString().trim();
                isNet = receiver.judgeNetIsConnected(RegisterActivity.this);
               if(Validator.isMobile(phone)){
                    if(isNet){
                        getverification(phone);
                    }else {
                        Toast.makeText(RegisterActivity.this,"当前没有网络",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.textview_register_register:
                isNet = receiver.judgeNetIsConnected(RegisterActivity.this);
                String mobile = inputPhoneNum.getText().toString().trim();
                String validPhoneCode = inputVerification.getText().toString().trim();
                String username = inputUserName.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String repassword = inputAginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                Toast.makeText(RegisterActivity.this, "用户名不能为空",
                        Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(RegisterActivity.this, "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                }  else if(!Validator.isMobile(mobile)){
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号",
                            Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(validPhoneCode)){
                    Toast.makeText(RegisterActivity.this, "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if (!password.equals(repassword)) {
                    Toast.makeText(RegisterActivity.this, "两次密码不一致",
                            Toast.LENGTH_SHORT).show();
                } else if (!isAgreeAgreement) {
                    Toast.makeText(RegisterActivity.this, "请同意《用户注册协议》",
                            Toast.LENGTH_SHORT).show();
                }else {
                    if (isNet) {
                        register(mobile, username, password,validPhoneCode);// 注册
                        /*Intent loginToForgetpassword = new Intent(RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(loginToForgetpassword);*/
                    } else {
                        Toast.makeText(RegisterActivity.this, "当前没有网络",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.relativelayout_register_seepassword:
                if (isSeePassword) {
                    // 如果当前密码显示，就把密码设置为文本不可见
                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isSeePassword = false;
                } else {
                    // 如果当前密码不显示，就把密码设置为文本可见
                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isSeePassword = true;
                }
                break;

            default:
                break;
        }
    }

    /**
     * 注册
     */
    private void register(final String str1, String str2, String str3,String str4) {
        // TODO Auto-generated method stub
        progressDialog = new ProgressDialog(RegisterActivity.this);//创建加载图片对象
        progressDialog.setMessage("正在加载，请稍候...");//加载

        progressDialog.show();
        RequestParams registerParams = new RequestParams(UrlUtils.Reg);
        registerParams.addBodyParameter("mobile", str1);
        registerParams.addBodyParameter("username", str2);
        registerParams.addBodyParameter("password", str3);
        registerParams.addBodyParameter("validPhonCode", str4);

        x.http().post(registerParams, new CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(RegisterActivity.this, "取消注册",
                        Toast.LENGTH_SHORT).show();
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
                    String success = jsonObject.getString("success");
                    String info = jsonObject.getString("info");
                    if ("1".equals(success)) {
                        progressDialog.dismiss();
                        String token = jsonObject.getString("token");//token数据用户唯一标识符
                        /**
                         *Toast.makeText( )用来显示信息*/

                        Toast.makeText(RegisterActivity.this, "注册成功",
                                Toast.LENGTH_SHORT).show();
                      /*  editor.putString("phone", str1);
                        editor.putString("userName", str1);
                        editor.putString("token", token);*/
                        editor.commit();
                        finish();

                    } else {
                        Toast.makeText(RegisterActivity.this, info,
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();//关闭加载
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

    /**
     * 获取验证码
     */
    private void getverification(String str) {
        // TODO Auto-generated method stub
        getVerifivationCodeTv.setBackgroundResource(R.drawable.reset);// 点击获取验证码后把背景换为灰色
        getVerifivationCodeTv.setText("60");// 把内容换为数字
        getVerificationCode.setEnabled(false);// 设置当前获取验证码按钮为不可点击
        handler.postDelayed(runnable, 1000);// 每过一秒
        RequestParams getVerificationParams = new RequestParams(
                UrlUtils.GET_VERIFICATION_CODE);
        getVerificationParams.addBodyParameter("phone", str);
        x.http().post(getVerificationParams, new CommonCallback<String>() {
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
                    System.out.println("获取验证码：" + arg0);
                    JSONObject jsonObject = new JSONObject(arg0);
                    String success = jsonObject.getString("success");
                    String info = jsonObject.getString("info");
                    if("1".equals(success)){
                        Toast.makeText(RegisterActivity.this,info,Toast.LENGTH_SHORT).show();
                        System.out.println("获取成功");
                    }else {
                        Toast.makeText(RegisterActivity.this,info,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 倒计时
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            reclen--;
            getVerifivationCodeTv.setText("" + reclen);
            if (reclen == 0) {
                // 当倒计时到0以后，把背景换为黄色，设置当前按钮可点击，设置字为“获取验证码”,把倒计时重新设置为60
                getVerifivationCodeTv
                        .setBackgroundResource(R.drawable.loginbackgroundpic);
                getVerifivationCodeTv
                        .setText("获取验证码");
                getVerificationCode.setEnabled(true);
                reclen = 60;
            } else {
                handler.postDelayed(this, 1000);// 每过一秒减一
            }
        }
    };
}
