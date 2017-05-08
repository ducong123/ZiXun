package com.nainaiwang.myreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class JudgeNetIsConnectedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}

	/**
	 * 判断网络连接是否成功
	 *
	 * @param context
	 *            上下文对象
	 * @return 网络连接是否成功
	 */
	public static boolean judgeNetIsConnected(Context context) {
		// 得到连接管理器对象
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}

		return networkInfo.isConnected();
	}

}
