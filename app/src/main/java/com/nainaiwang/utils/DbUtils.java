package com.nainaiwang.utils;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;

public class DbUtils {

	private static DaoConfig daoConfig;

	//单例模式获取数据库
	public static DaoConfig getDaoConfig() {
		if (daoConfig == null) {
			daoConfig = new DaoConfig().setAllowTransaction(true)
					.setDbName("nainaizixun.db").setDbVersion(1)
					.setDbUpgradeListener(new DbManager.DbUpgradeListener() {

						@Override
						public void onUpgrade(DbManager arg0, int arg1, int arg2) {
							// TODO Auto-generated method stub

						}
					});
		}
		return daoConfig;
	}

}
