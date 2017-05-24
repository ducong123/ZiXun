package com.nainaiwang.zixun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.adapter.ChannelsAdapter;
import com.nainaiwang.adapter.HomePageChannelAdapter;
import com.nainaiwang.bean.HomePageChannels;
import com.nainaiwang.fragment.HomePageChannelFragment;
import com.nainaiwang.utils.DbUtils;
import com.nainaiwang.utils.NetInfoUtils;
import com.nainaiwang.utils.UrlUtils;
import com.nainaiwang.widget.MyViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static String myCookieValue;
    private TextView search, showChannels, /*mainRegister,*/myCenter;
    private MyViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout showChannelsLayout, left;
    private RelativeLayout rl;
    private GridView noScrollGridView;

    private HomePageChannelAdapter adapter;
    private List<Fragment> fragmentList;
    private List<HomePageChannels> stringList = new ArrayList<>();

    private boolean open = false;//true代表打开频道选择，false代表没有打开频道选择

    private final int animaltionTime = 300;//设置动画时间

    private ChannelsAdapter channelsAdapter;

    private AlertDialog netDialog;//网络对话框

    private DbManager db;//数据库管理器
    private boolean isLogin;

    @Override
    public  void onResume(){
        super.onResume();
        isLogin= true;
    }

//    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
//                //网络断开
////                popNetDialog();
//            } else {
//                //网络连接
//                downJson();
////                netDialog.dismiss();
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        registerNetReceiver();

        initView();
        initData();

        // 判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initView() {
        search = (TextView) findViewById(R.id.textview_main_search);
        viewPager = (MyViewPager) findViewById(R.id.viewpager_main_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_main_tablayout);
        showChannels = (TextView) findViewById(R.id.textview_main_showchannel);
       /* mainRegister =(TextView)findViewById(R.id.textview_main_register);*///初始化注册按钮控件
        showChannelsLayout = (LinearLayout) findViewById(R.id.linearlayout_main_showchannel);
        left = (LinearLayout) findViewById(R.id.linearlayout_channel);
        rl = (RelativeLayout) findViewById(R.id.relativelayout_main_rl);
        noScrollGridView = (GridView) findViewById(R.id.noscrollgridview_main_channels);

        myCenter = (TextView)findViewById(R.id.myCenter);//个人中心按钮
        myCenter.setOnClickListener(this);
      /*  mainRegister.setOnClickListener(this);*/
    }

    private void initData() {

        //数据库设置
        DbManager.DaoConfig daoConfig = DbUtils.getDaoConfig();
        db = x.getDb(daoConfig);


        if (NetInfoUtils.isNetworkConnected(MainActivity.this)) {
            downJson();
        } else {
            findData();
//            popNetDialog();
        }


        search.setOnClickListener(this);
        showChannelsLayout.setOnClickListener(this);

    }

    private void popNetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_nonet, null);
        TextView cancle = (TextView) view.findViewById(R.id.dialog_nonet_cancle);
        TextView goSet = (TextView) view.findViewById(R.id.dialog_nonet_goset);
        cancle.setOnClickListener(this);
        goSet.setOnClickListener(this);
        builder.setView(view);
        netDialog = builder.create();
        netDialog.show();
    }
private SharedPreferences.Editor editor;
    /**
     * 下载json数据
     */
    private void downJson() {
        stringList.clear();

        try {
            db.delete(HomePageChannels.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        RequestParams jsonParams = new RequestParams(UrlUtils.HOMEPAGE);
        x.http().post(jsonParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
              /*  DbCookieStore instance = DbCookieStore.INSTANCE;
                List<HttpCookie> cookies = instance.getCookies();
                for(HttpCookie cookie:cookies){
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    if("JSESSIONID".equals(name)){
                        String myCookie = value;
                        String sessionId = name;
                        ZiXunApplication.myCookieValue = value;

                    }
                }获取sessionId*/
                try {
                    Log.i("MainActivity",s);
                    JSONObject jsonObject1 = new JSONObject(s);
                    String code = jsonObject1.getString("success");
                  /*  String token = jsonObject1.getString("success");*/
                    if (code.equals("1")) {
                        /*editor.putString("token",token);
                        editor.commit();*/
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("info");
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            HomePageChannels homePageChannels = new HomePageChannels();
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                            homePageChannels.setId(jsonObject2.getString("id"));
                            homePageChannels.setName(jsonObject2.getString("name"));
                            stringList.add(homePageChannels);
                            try {
                                db.save(homePageChannels);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }


                        setFragmentNum();
                    } else {
                        Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void setFragmentNum() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            Bundle bundle = new Bundle();
            HomePageChannelFragment fragment = new HomePageChannelFragment();
            bundle.putString("key", stringList.get(i).getId());
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < stringList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(stringList.get(i).getName()));
        }

        FragmentManager manager = getSupportFragmentManager();
        viewPager.setOffscreenPageLimit(1);
        adapter = new HomePageChannelAdapter(manager, fragmentList, stringList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        channelsAdapter = new ChannelsAdapter(MainActivity.this, stringList);
        noScrollGridView.setAdapter(channelsAdapter);
        noScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOrClose();
                viewPager.setCurrentItem(position);
            }
        });
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.myCenter:
              Intent checkLogegister = new Intent(MainActivity.this,
                        MyCenterActivity.class);
                startActivity(checkLogegister);/*跳转到个人中心*/
                break;
            case R.id.textview_main_search:
                Intent mainToSearch = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(mainToSearch);
                break;
            case R.id.linearlayout_main_showchannel:
                openOrClose();
                break;
            case R.id.dialog_nonet_cancle:
                netDialog.dismiss();//取消对话框
                break;

            case R.id.dialog_nonet_goset:
                //设置网络
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//打开手机系统网络设置页面
                netDialog.dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 打开或者关闭频道选择
     */
    private void openOrClose() {
        if (open == false) {
            //如果是关闭状态的话就打开频道选择
            showChannels.setBackgroundResource(R.mipmap.shangla);
            leftAnimal();
            fragmentAnimal();
            open = true;
        } else {
            //如果是打开状态的话就关闭频道选择
            showChannels.setBackgroundResource(R.mipmap.xiala);
            leftAnimal();
            fragmentAnimal();
            open = false;
        }
    }

    /**
     * fragment的动画
     */
    private void fragmentAnimal() {
        if (open == false) {
            viewPager.setScanScroll(false);
            rl.setVisibility(View.VISIBLE);
            int height = viewPager.getMeasuredHeight();
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -height, 0f);
            translateAnimation.setDuration(animaltionTime);
            translateAnimation.setInterpolator(new LinearInterpolator());
            rl.startAnimation(translateAnimation);
        } else {
            viewPager.setScanScroll(true);
            int height = viewPager.getMeasuredHeight();
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0f, -height);
            translateAnimation.setDuration(animaltionTime);
            rl.startAnimation(translateAnimation);
            rl.setVisibility(View.GONE);
        }
    }


    /**
     * 左边的动画
     */
    private void leftAnimal() {
        if (open == false) {
            left.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation1 = new AlphaAnimation(0, 1);
            alphaAnimation1.setDuration(animaltionTime);
            left.startAnimation(alphaAnimation1);

            AlphaAnimation alphaAnimation2 = new AlphaAnimation(1, 0);
            alphaAnimation2.setDuration(animaltionTime);
            tabLayout.startAnimation(alphaAnimation2);
            tabLayout.setVisibility(View.GONE);

        } else {
            AlphaAnimation alphaAnimation3 = new AlphaAnimation(1, 0);
            alphaAnimation3.setDuration(animaltionTime);
            left.startAnimation(alphaAnimation3);
            left.setVisibility(View.GONE);

            tabLayout.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation4 = new AlphaAnimation(0, 1);
            alphaAnimation4.setDuration(animaltionTime);
            tabLayout.startAnimation(alphaAnimation4);
        }

    }

    private void findData() {
        stringList.clear();
        try {
            stringList = db.findAll(HomePageChannels.class);
            if (stringList != null) {
                setFragmentNum();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        netDialog.dismiss();
//        unregisterReceiver(connectionReceiver);
    }
}
