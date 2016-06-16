package com.maptool.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * log类
 * @author mazhibin
 */
public class L {
	static Context context = null;
	
	public static void setContext(Context context){
		L.context = context;
	}
	
	/**
	 * debug
	 */
	public static void d(String msg){
		Log.d(Const.app_name, msg);
	}
	
	public static void d(Object o,String msg){
		Log.d(Const.app_name+"_"+o.getClass().getSimpleName(),msg);
	}
	
	/**
	 * error
	 */
	public static void e(String msg){
		Log.e(Const.app_name, msg);
	}
	
	public static void e(Object o,String msg){
		Log.e(Const.app_name+"_"+o.getClass().getSimpleName(),msg);
	}
	
	/**
	 * toast
	 */
	public static void showToast(String msg){
		L.d(msg);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
