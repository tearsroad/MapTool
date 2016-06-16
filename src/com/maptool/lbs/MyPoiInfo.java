package com.maptool.lbs;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPoiInfo extends PoiInfo{
	private static final long serialVersionUID = 1L;
	
	public String device_id;
	public String device_pos;
	public String start_time;
	public String contacts;
	public String phone;
	public String device_type;
	public String sentinel;
	public String buy_type;
	
	public MyPoiInfo(JSONObject jo) throws JSONException {
		super(jo);
		
		if(jo == null) return;
		device_id = getJsonStr(jo, "device_id");
		device_pos = getJsonStr(jo, "device_pos");
		start_time = getJsonStr(jo, "start_time");
		contacts = getJsonStr(jo, "contacts");
		phone = getJsonStr(jo, "phone");
		device_type = getJsonStr(jo, "device_type");
		sentinel = getJsonStr(jo, "sentinel");
		buy_type = getJsonStr(jo, "buy_type");
	}
	
	private static String getJsonStr(JSONObject jo,String key) throws JSONException{
		if(jo.has(key)){
			return jo.getString(key);
		}
		return "";
	}
}
