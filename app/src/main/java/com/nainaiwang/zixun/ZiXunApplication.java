package com.nainaiwang.zixun;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Administrator on 2016/12/29.
 */
public class ZiXunApplication extends Application {

    public static String myCookieValue;

    @Override
    public void onCreate() {
        super.onCreate();

        initXUtils();//初始化xUtils
    }

    private void initXUtils() {
        x.Ext.init(this);
    }
}
