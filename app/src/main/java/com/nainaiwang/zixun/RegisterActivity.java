package com.nainaiwang.zixun;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.myreceiver.JudgeNetIsConnectedReceiver;
import com.nainaiwang.utils.CanvasImageTask;
import com.nainaiwang.utils.UrlUtils;
import com.nainaiwang.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.http.annotation.HttpRequest;
import org.xutils.http.annotation.HttpResponse;
import org.xutils.x;

import java.io.File;

public class RegisterActivity extends Activity implements OnClickListener {

    private TextView agreement, registerBtn, getVerificationCodeTv;// 《用户注册协议》，注册,获取验证码里面的字
    private CheckBox iAgree;// 是否同意条款
    private RelativeLayout back, getVerificationCode, seePassword;// 返回，获取验证码，可视密码
    private EditText inputPhoneNum, inputVerification, inputPassword,inputUsername,rePassword,inputImgVer;// 输入手机号，输入验证码，输入密码
    private ImageView imgcode;

    private boolean isSeePassword = false;// 点击眼睛，是否显示密码,默认不显示
    private boolean isAgreeAgreement = true;
    private boolean isNet;// 是否有网络

    private Handler handler = new Handler();
    private int reclen = 60;// 倒计时60秒

    private SharedPreferences sp;
    private Editor editor;

    private ProgressDialog progressDialog;// 进度框
    private JudgeNetIsConnectedReceiver receiver;// 广播接受者

   private String path="http://ceshi.nainaiwang.com/user/login/captcha";
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
         imgcode.setTag(path);
           new CanvasImageTask().execute(imgcode);

    }



    /**
     * 初始化控件
     */
    private void initView() {
        // TODO Auto-generated method stub
        back = (RelativeLayout) findViewById(R.id.relativelayout_register_back);
        seePassword = (RelativeLayout) findViewById(R.id.relativelayout_register_seepassword);
        getVerificationCode = (RelativeLayout) findViewById(R.id.relativelayout_register_getverification);
        agreement = (TextView) findViewById(R.id.textview_register_agressment);
        registerBtn = (TextView) findViewById(R.id.textview_register_register);
        getVerificationCodeTv = (TextView) findViewById(R.id.textview_register_getverificationtv);
        iAgree = (CheckBox) findViewById(R.id.checkbox_register_agressment);
        inputPhoneNum = (EditText) findViewById(R.id.edittext_register_inputphone);
        inputPassword = (EditText) findViewById(R.id.edittext_register_inputpassword);
        inputVerification = (EditText) findViewById(R.id.edittext_register_verification);
        inputUsername = (EditText)findViewById(R.id.edittext_register_inputUsername) ;
        rePassword =(EditText)findViewById(R.id.edittext_register_inputrepassword);
        imgcode =(ImageView)findViewById(R.id.imageview_edit_imgCode);//图形验证码显示
        inputImgVer=(EditText)findViewById(R.id.edittext_img_code);//输入图形验证码

        back.setOnClickListener(this);
        getVerificationCode.setOnClickListener(this);
        agreement.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        seePassword.setOnClickListener(this);
        imgcode.setOnClickListener(this);
        imgcode.setOnClickListener(this);
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
                String phoneNum = inputPhoneNum.getText().toString().trim();
                String st2 = inputImgVer.getText().toString().trim();//获取图形验证码的值
                isNet = receiver.judgeNetIsConnected(RegisterActivity.this);
              if(!Validator.isMobile(phoneNum)){
                    Toast.makeText(RegisterActivity.this, "手机号码不正确，请重新输入",
                            Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(st2)){
                Toast.makeText(RegisterActivity.this, "验证码不能是空的",
                        Toast.LENGTH_SHORT).show();
                }else {
                  if (isNet) {
                      getverification(phoneNum,st2);// 获取验证码
                  } else {
                      Toast.makeText(RegisterActivity.this, "当前没有网络",
                              Toast.LENGTH_SHORT).show();
                  }
              }
                break;
            case R.id.textview_register_register:
                isNet = receiver.judgeNetIsConnected(RegisterActivity.this);
                String phoneNum2 = inputPhoneNum.getText().toString().trim();
                String verificationCode = inputVerification.getText().toString()
                        .trim();
                String password = inputPassword.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();
                String repassword = rePassword.getText().toString().trim();
                String agent= null;
                if(isAgreeAgreement){
                    agent="1";
                }else {
                    agent="0";
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(phoneNum2)) {
                    Toast.makeText(RegisterActivity.this, "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(RegisterActivity.this, "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                } else if(!repassword.equals(password)){
                    Toast.makeText(RegisterActivity.this, "密码不一致",
                            Toast.LENGTH_SHORT).show();
                }else if (!isAgreeAgreement) {
                    Toast.makeText(RegisterActivity.this, "请同意《用户注册协议》",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (isNet) {
                        register(phoneNum2, verificationCode, password,username,agent);// 注册
                    } else {
                        Toast.makeText(RegisterActivity.this, "当前没有网络",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.textview_register_agressment:
                Intent registerToTest = new Intent(RegisterActivity.this,
                        LoginActivity.class);
                registerToTest.putExtra("name", "用户注册协议");
                registerToTest.putExtra("activity", "register");
                startActivity(registerToTest);
                // 查看输入的密码
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
            case  R.id.imageview_edit_imgCode:
                imgcode.setTag(path);
                new CanvasImageTask().execute(imgcode);//CanvasImageTask()异步获取图片
              /*  getImg();*/
                break;

            default:
                break;
        }
    }



    /**
     * 注册
     */
    private void register(final String str1, String str2, final String str3, final String str4,final String str5) {
        // TODO Auto-generated method stub
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("正在加载，请稍候...");
        progressDialog.show();
        RequestParams registerParams = new RequestParams(UrlUtils.Reg);
        registerParams.addBodyParameter("mobile", str1);
        registerParams.addBodyParameter("validPhoneCode", str2);
        registerParams.addBodyParameter("password", str3);
        registerParams.addBodyParameter("username", str4);
        registerParams.addBodyParameter("agent", str5);
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
                    if ("0".equals(success)) {
                        Toast.makeText(RegisterActivity.this, info,
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();//关闭
                    } else {
                        progressDialog.dismiss();
                        String token = jsonObject.getString("token");
                        Toast.makeText(RegisterActivity.this, "注册成功",
                                Toast.LENGTH_SHORT).show();
                        editor.putString("mobile", str1);
                        editor.putString("username", str4);
                        editor.putString("password",str3);
                        editor.putString("token", token);
                        editor.putString("agent",str5);
                        editor.commit();
                        finish();
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
/*图形验证码*//*
public void getImg(){

    RequestParams getVerificationParams = new RequestParams(
            UrlUtils.Img);
    x.http().post(getVerificationParams, new CommonCallback<ImageView>() {
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
        public void onSuccess(ImageView arg0) {
            // TODO Auto-generated method stub
                System.out.println("获取图形验证码：" + arg0);


        }
    });
}*/

/*图形验证码 end*/

    /**
     * 获取验证码
     */
    private void getverification(final String str, final String str2) {
        // TODO Auto-generated method stub

        RequestParams getVerificationParams = new RequestParams(
                UrlUtils.GET_VERIFICATION_CODE);
        getVerificationParams.addHeader("Cookie", "JSESSIONID=" + ZiXunApplication.myCookieValue);
        getVerificationParams.addHeader("Content-Type", "application/json;charset=UTF-8");
        getVerificationParams.setUseCookie(true);
        getVerificationParams.addBodyParameter("phone", str);
        getVerificationParams.addBodyParameter("captcha", str2);

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
                        Toast.makeText(RegisterActivity.this, info,
                                Toast.LENGTH_SHORT).show();
                        getVerificationCodeTv.setBackgroundResource(R.drawable.reset);// 点击获取验证码后把背景换为灰色
                        getVerificationCodeTv.setText("60");// 把内容换为数字
                        getVerificationCode.setEnabled(false);// 设置当前获取验证码按钮为不可点击
                        handler.postDelayed(runnable, 1000);// 每过一秒
                        editor.putBoolean("isLogin", true);//
                        editor.putString("phone", str);
                        editor.putString("captcha", str2);
                        editor.commit();
                    }else {
                       /* getVerificationCodeTv.setBackgroundResource(R.drawable.reset);// 点击获取验证码后把背景换为灰色
                        getVerificationCodeTv.setText("");// 把内容换为数字
                        getVerificationCode.setEnabled(false);// 设置当前获取验证码按钮为不可点击
                        handler.postDelayed(runnable, 1000);// 每过一秒*/
                        Toast.makeText(RegisterActivity.this,info,
                                Toast.LENGTH_SHORT).show();
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
            getVerificationCodeTv.setText("" + reclen);
            if (reclen == 0) {
                // 当倒计时到0以后，把背景换为黄色，设置当前按钮可点击，设置字为“获取验证码”,把倒计时重新设置为60
                getVerificationCodeTv
                        .setBackgroundResource(R.drawable.loginbackgroundpic);
                getVerificationCodeTv.setText("获取验证码");
                getVerificationCode.setEnabled(true);
                reclen = 60;
            } else {
                handler.postDelayed(this, 1000);// 每过一秒减一
            }
        }
    };

}
