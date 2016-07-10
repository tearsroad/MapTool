package com.maptool.lbs;

import org.json.JSONException;
import org.json.JSONObject;

import com.maptool.common.L;

public class AppInfoItem{
	public String versionName;
	
	public int versionCode = 0;
	public String packageName;
	public String url;
	public String history;
	public boolean isForceUpdate = false;

	public AppInfoItem(JSONObject jo) throws JSONException {
		if(jo!=null&&!"".equals(jo)){
			versionName = jo.getString("versionName");
			versionCode = jo.getInt("versionCode");
			packageName = jo.getString("packageName");
			url = jo.getString("url");
			//"1：测试；\\n2：测试2；\\n3：测试3；"
			L.e("AppInfo", "history1:"+jo.getString("history"));
			history = gethistory(jo.getString("history"));
			L.e("AppInfo", "history2:"+history);
			isForceUpdate = isForceUpdate(jo.getString("forceUpdate"));
		}
	}
	private String gethistory(String s){
		if(s==null)return s;
//		int i = s.indexOf("\\n");
//		L.e("index:"+i);
		s = s.replaceAll("\\\\n","\n");
//		s = s.replaceAll("\\\\\\\\", "\\\\");
		return s;
	}
	private boolean isForceUpdate(String s){
		if("y".equals(s)||"Y".equals(s))
			return true;
		else
			return false;
	}
}
