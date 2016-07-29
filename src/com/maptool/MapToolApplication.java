package com.maptool;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;

import android.app.Application;

public class MapToolApplication extends Application {
	public static boolean isAppUpdateInfoShow = false;
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		CrashReport.initCrashReport(getApplicationContext(), "900041916", false);
	}
}
