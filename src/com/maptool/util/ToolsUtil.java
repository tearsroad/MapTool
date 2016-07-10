package com.maptool.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ToolsUtil {
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    public static boolean isStrNull(String str){
    	if(str==null||"".equals(str)||" ".equals(str))
    		return true;
    	return false;
    }
    /**
     * 全角转为半角字符
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
    /** 
     * 返回当前程序版本名 
     */  
    public static String getAppVersionName(Context context) {  
        String versionName = "";  
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versionName = pi.versionName;  
            if (versionName == null || versionName.length() <= 0) {  
                return "";  
            }  
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versionName;  
    } 
    /** 
     * 返回当前程序版本号
     */  
    public static int getAppVersionCode(Context context) {  
        int versioncode =0;  
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versioncode = pi.versionCode;
            
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versioncode;  
    } 
    /**
     * 返回当前应用包名
     * @param context
     * @return
     */
    public static String getPackName(Context context){
    	return context.getPackageName();
    }
}
