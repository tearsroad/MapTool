package com.maptool.lbs;

import org.json.JSONException;
import org.json.JSONObject;

public class StockoutPoiInfo{

	public int goods_id = 0;

	public StockoutPoiInfo(JSONObject jo) throws JSONException {
		if(jo.has("goods_id")){
			goods_id = jo.getInt("goods_id");
		}
	}

}
