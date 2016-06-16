package com.maptool.lbs;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PoiInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public int uid;
	public int geotable_id;
	public String title;
	public String address;
	public String province;
	public String city;
	public String district;
	public int coord_type;
	public double longitude;
	public double latitude;
	public int distance;
	public int weight;
	public int type;
	
	public PoiInfo(JSONObject jo) throws JSONException {
		if(jo == null) return;
		uid = jo.getInt("uid");
		geotable_id = jo.getInt("geotable_id");
		title = jo.getString("title");
		address = jo.getString("address");
		province = jo.getString("province");
		city = jo.getString("city");
		district = jo.getString("district");
		coord_type = jo.getInt("coord_type");
		
		//注意返回的location格式
		JSONArray locationArr = jo.getJSONArray("location");
		longitude = locationArr.getDouble(0);
		latitude = locationArr.getDouble(1); 
		
		distance = jo.getInt("distance");
		weight = jo.getInt("weight");
		type = jo.getInt("type");
	}
}