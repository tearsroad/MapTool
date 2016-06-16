package com.maptool;

import com.baidu.mapapi.SDKInitializer;
import com.maptool.common.L;

import android.app.Application;

public class MapApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		SDKInitializer.initialize(this);
		
	}
}
