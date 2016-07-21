package com.maptool.lbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.maptool.common.Const;
import com.maptool.common.ErrorFactory;
import com.maptool.common.L;
import com.maptool.common.MyError;

/**
 * lbs云搜索类
 * 
 * @author mazhibin
 */
public class MyLBS extends BaiduLBS {
	private static final String TAG = "MyLBS";
	public static final int Message_Error = -1;
	public static final int Message_NearGoods = 1001;

	// 全部计生用品表id
	protected int goodsTableId;

	// 缺货计生用品表id
	protected int stockoutTableId;

	/**
	 * 获取周围用品回调类
	 * 
	 * @author mazhibin
	 */
	public interface LBSInteractionListener {
		public void onGetNearbyGoods(MyError err, List<MyPoiInfo> list);

		public void onGetStockout(MyError err, List<StockoutPoiInfo> list);

		public void onReportStockout(MyError err);
		
		public void onGetAppInfo(MyError err, List<AppInfoItem> list);
	}

	public MyLBS(int goodsTableId, int stockoutTableId) {
		this.goodsTableId = goodsTableId;
		this.stockoutTableId = stockoutTableId;
	}

	/**
	 * 获取周围物品
	 */
	public void searchNearGoods(final int radius, final double longitude,
			final double latitude, final LBSInteractionListener listener) {
		new Thread() {
			@Override
			public void run() {
				JSONObject jo = null;
				String strmsg = nearbySearch(goodsTableId, radius, longitude,
						latitude);
				try {
					jo = json(strmsg);
				} catch (JSONException e) {
					listener.onGetNearbyGoods(
							ErrorFactory.ErrorJson.setShowMsg(strmsg), null);
					return;
				}
				
				if(jo==null || !jo.has("status")){
					listener.onGetNearbyGoods(
							ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,返回信息为空"), null);
					return;
				}

				try {
					if (jo.getInt("status") != 0) {
						listener.onGetNearbyGoods(
								ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,status!=0"), null);
						return;
					}

					List<MyPoiInfo> list = new ArrayList<MyPoiInfo>();
					JSONArray contents = jo.getJSONArray("contents");
					for (int i = 0, n = contents.length(); i < n; i++) {
						JSONObject poiItem = contents.getJSONObject(i);
						MyPoiInfo info = new MyPoiInfo(poiItem); // 从json中获取PoiInfo
						list.add(info);
					}
					listener.onGetNearbyGoods(null, list);
				} catch (JSONException e) {
					listener.onGetNearbyGoods(
							ErrorFactory.ErrorJson.setShowMsg("JSON解析失败！"), null);
				}
			}
		}.start();
	}

	/**
	 * 获取缺货信息
	 */
	public void getStockout(final LBSInteractionListener listener) {
		new Thread() {
			@Override
			public void run() {
				JSONObject jo = null;
				String strMst = listData(Const.stockoutTableId, null);
				try {
					jo = json(strMst);
				} catch (JSONException e) {
					listener.onGetStockout(
							ErrorFactory.ErrorJson.setShowMsg(strMst), null);
					return;
				}
				
				if(jo==null || !jo.has("status")){
					listener.onGetStockout(
							ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,返回信息为空"), null);
					return;
				}

				try {
					if (jo.getInt("status") != 0) {
						listener.onGetStockout(
								ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,status!=0"), null);
						return;
					}

					List<StockoutPoiInfo> list = new ArrayList<StockoutPoiInfo>();
					if(jo.getInt("total") != 0){
						JSONArray contents = jo.getJSONArray("pois");
						for (int i = 0, n = contents.length(); i < n; i++) {
							JSONObject poiItem = contents.getJSONObject(i);
							StockoutPoiInfo info = new StockoutPoiInfo(poiItem);
							list.add(info);
						}
					}
					listener.onGetStockout(null, list);
				} catch (JSONException e) {
					listener.onGetStockout(
							ErrorFactory.ErrorJson.setShowMsg("JSON解析失败！"), null);
				}
			}
		}.start();
	}

	/**
	 * 缺货上报
	 */
	public void reportStockout(final MyPoiInfo info,final String reasonStr,
			final LBSInteractionListener listener) {
		new Thread() {
			@Override
			public void run() {
				final Map<String, String> params = new HashMap<String, String>();
				params.put("goods_id", "" + info.uid);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timeStr = format.format(System.currentTimeMillis());
				params.put("time",timeStr);
				params.put("reason", reasonStr);

				// 先看看是否已经上报
				isStockout(info, new IsStockoutListener() {
					@Override
					public void onGetIsStockout(MyError err, boolean isStockout) {
						if (err == null) {
							if (isStockout == false) {
								Log.e(TAG, "还未上报");
								JSONObject jo = null;
								String strMsg = createData(Const.stockoutTableId,
										info.title, info.latitude,
										info.longitude, info.coord_type,
										params);
								try {
									jo = json(strMsg);
								} catch (JSONException e) {
									listener.onReportStockout(ErrorFactory.ErrorJson
											.setShowMsg(strMsg));
								}
								
								if(jo==null || !jo.has("status")){
									listener.onReportStockout(
											ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,返回信息为空"));
									return;
								}

								try {
									if (jo.getInt("status") != 0) {
										listener.onReportStockout(ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,status!=0"));
										return;
									}
									listener.onReportStockout(null);
								} catch (JSONException e) {
									listener.onReportStockout(ErrorFactory.ErrorJson
											.setException(e));
								}
							}
						}else{
							listener.onReportStockout(err);
						}
					}
				});
			}
		}.start();
	}

	/**
	 * 判断是否缺货回调
	 * 
	 * @author mzb
	 */
	public interface IsStockoutListener {
		public void onGetIsStockout(MyError err, boolean isStockout);
	}

	/**
	 * 判断是否已经上报
	 */
	public void isStockout(final MyPoiInfo info,
			final IsStockoutListener listener) {
		new Thread() {
			@Override
			public void run() {
				JSONObject jo = null;
				Map<String, String> params = new HashMap<String, String>();
				params.put("goods_id", "" + info.uid);
				String strMsg = listData(Const.stockoutTableId, params);
				try {
					jo = json(strMsg);
				} catch (JSONException e) {
					listener.onGetIsStockout(
							ErrorFactory.ErrorJson.setShowMsg(strMsg), false);
					return;
				}
				
				if(jo==null || !jo.has("status")){
					listener.onGetIsStockout(
							ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,返回信息为空"), false);
					return;
				}

				try {
					if (jo.getInt("status") != 0) {
						listener.onGetIsStockout(
								ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,status!=0"), false);
						return;
					}
					Log.e(TAG,jo.toString());
					if (jo.has("pois") && !jo.isNull("pois")&& jo.getJSONArray("pois").length() > 0) {
						JSONArray pois = jo.getJSONArray("pois");
						for(int i=0,n=pois.length();i<n;i++){
							Log.e(TAG,"goods_id "+pois.getJSONObject(i).getString("goods_id"));
							Log.e(TAG,"uid "+info.uid);
							if(pois.getJSONObject(i).getString("goods_id").equals(""+info.uid)){
								listener.onGetIsStockout(null, true); // 已经上报
								return;
							}
						}
					}
					listener.onGetIsStockout(null, false); // 未上报
				} catch (JSONException e) {
					listener.onGetIsStockout(
							ErrorFactory.ErrorJson.setShowMsg("JSON解析失败！"), false);
				}
			}
		}.start();
	}
	
	
	//new 20160710
	//app升级模块
	/**
	 * 获取版本最新信息
	 */
	public void getAppInfo(final LBSInteractionListener listener) {
		new Thread() {
			@Override
			public void run() {
				JSONObject jo = null;
				String strMsg = listData(Const.appinfoTableId, null);
				try {
					jo = json(strMsg);
					if(jo!=null)
						L.e(jo.toString());
				} catch (JSONException e) {
					listener.onGetAppInfo(
							ErrorFactory.ErrorJson.setShowMsg(strMsg), null);
				}
				
				if(jo==null || !jo.has("status")){
					listener.onGetAppInfo(
							ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,返回信息为空"), null);
					return;
				}

				try {
					if (jo.getInt("status") != 0) {
						listener.onGetAppInfo(
								ErrorFactory.ErrorJson.setShowMsg("请求免费发放点出错,status!=0"), null);
						return;
					}

					List<AppInfoItem> list = new ArrayList<AppInfoItem>();
					if(jo.getInt("total") != 0){
						JSONArray contents = jo.getJSONArray("pois");
						for (int i = 0, n = contents.length(); i < n; i++) {
							JSONObject poiItem = contents.getJSONObject(i);
							AppInfoItem info = new AppInfoItem(poiItem);
							list.add(info);
						}
					}
					listener.onGetAppInfo(null, list);
				} catch (JSONException e) {
					listener.onGetAppInfo(
							ErrorFactory.ErrorJson.setShowMsg("JSON解析失败！"), null);
				}
			}
		}.start();
	}
}
