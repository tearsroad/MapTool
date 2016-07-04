package com.maptool.lbs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.maptool.Info_ffpz1_Activity;
import com.maptool.Info_ffpz2_Activity;
import com.maptool.Info_ffpz3_Activity;
import com.maptool.Info_ffxs_rg_Activity;
import com.maptool.Info_ffxs_zz_Activity;

public class MyPoiInfo extends PoiInfo{
	private static final long serialVersionUID = 1L;
	
	public String device_id;
	public String device_pos;
	public String start_time;
	public String end_time;
	public String contacts;
	public String phone;
	public String device_type;
	public String sentinel;
	public String buy_type;
	public String ffxingshi;
	public String ffpinzhong;
	
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
		end_time = getJsonStr(jo, "end_time");
		ffxingshi = getJsonStr(jo, "ffxingshi");
		ffpinzhong = getJsonStr(jo, "ffpinzhong");
	}
	
	private static String getJsonStr(JSONObject jo,String key) throws JSONException{
		if(jo.has(key)){
			return jo.getString(key);
		}
		return "";
	}
	/**
	 * 获取这个点对应的发放品种对应的文章信息
	 * @return
	 */
	public List<FFMsgItem> getFfpingzhong(){
		List<FFMsgItem> list = new ArrayList<FFMsgItem>();
		if(ffpinzhong!=null)
		for(int i=0;i<ffpinzhong.length();i++){
			FFMsgItem article = getArticleByChar(ffpinzhong.charAt(i));
			if(article!=null)
				list.add(article);
		}
		return list;
	}
	/**
	 * 获取这个点对应的发放形式对应的文章信息
	 * @return
	 */
	public List<FFMsgItem> getFfxingshi(){
		List<FFMsgItem> list = new ArrayList<FFMsgItem>();
		if(ffxingshi!=null)
		for(int i=0;i<ffxingshi.length();i++){
			FFMsgItem article = getArticleByChar(ffxingshi.charAt(i));
			if(article!=null)
				list.add(article);
		}
		return list;
	}
	/**
	 * 1:安全套
	 * 2：外用避孕药
	 * 3：口服避孕药
	 * 4:自取
	 * 5：送货上门
	 * @param pz
	 * @return
	 */
	private FFMsgItem getArticleByChar(char pz){
		FFMsgItem article = null;
		switch (pz) {
		case '1':
			article = new FFMsgItem("安全套", Info_ffpz1_Activity.class);
			break;
		case '2':
			article = new FFMsgItem("外用避孕药", Info_ffpz2_Activity.class);
			break;
		case '3':
			article = new FFMsgItem("口服避孕药", Info_ffpz3_Activity.class);
			break;
		case '4':
			article = new FFMsgItem("人工", Info_ffxs_rg_Activity.class);
			break;
		case '5':
			article = new FFMsgItem("自助", Info_ffxs_zz_Activity.class);
			break;
		default:
			break;
		}
		return article;
	}
	/**
	 * 发放形式，文字描述
	 * @return
	 */
	public String getFfxingshiMsg(){
		String msg = null;
		List<FFMsgItem> list = getFfxingshi();
		if(list.size()>=1)
			msg = list.get(0).getTitle();
		if(list.size()>=2)
			msg = msg+"，"+list.get(1).getTitle();
		return msg;
	}
}
