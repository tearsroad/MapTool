package com.maptool.map;

import java.util.List;

import android.os.Handler;

import com.maptool.common.Const;
import com.maptool.common.MyError;
import com.maptool.lbs.AppInfoItem;
import com.maptool.lbs.MyLBS;
import com.maptool.lbs.MyLBS.IsStockoutListener;
import com.maptool.lbs.MyLBS.LBSInteractionListener;
import com.maptool.lbs.MyPoiInfo;
import com.maptool.lbs.StockoutPoiInfo;

public class LBSHepler implements LBSInteractionListener {

	Handler mHandler;

	// lbs搜索
	MyLBS mMyLBS;
	protected LBSInteractionListener mListener;
	
	//临时变量
	protected int radius;
	protected double longitude;
	protected double latitude;
	protected List<StockoutPoiInfo> stockoutList;

	public LBSHepler(Handler handler, LBSInteractionListener listener) {
		mHandler = handler;
		mListener = listener;

		mMyLBS = new MyLBS(Const.goodsTableId, Const.stockoutTableId);
	}

	public void searchNearby(int radius, double longitude, double latitude) {
		mMyLBS.searchNearGoods(radius, longitude, latitude, this);
		this.radius = radius;
		this.longitude = longitude;
		this.latitude = latitude;
//		mMyLBS.getStockout(this);
	}

	public void reportStockout(MyPoiInfo info,String reasonStr) {
		mMyLBS.reportStockout(info,reasonStr, this);
	}
	
	public void getStockout2(){
		mMyLBS.getStockout(this);
	}
	
	public void isisStockout(final MyPoiInfo info,
			final IsStockoutListener listener){
		mMyLBS.isStockout(info, listener);
	}
	public void getAppInfo(){
		mMyLBS.getAppInfo(this);
	}
	@Override
	public void onGetNearbyGoods(final MyError err, final List<MyPoiInfo> list) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(list!=null && stockoutList!=null){
					for(StockoutPoiInfo stockInfo:stockoutList){
						for(MyPoiInfo poiInfo:list){
							if(poiInfo.uid == stockInfo.goods_id){
								list.remove(poiInfo);
								break;
							}
						}
					}
				}
				mListener.onGetNearbyGoods(err, list);
			}
		});
	}

	@Override
	public void onReportStockout(final MyError err) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onReportStockout(err);
			}
		});
	}

	@Override
	public void onGetStockout(final MyError err,final List<StockoutPoiInfo> list) {
//		this.stockoutList = list;
//		mMyLBS.searchNearGoods(radius, longitude, latitude, this);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onGetStockout(err, list);
			}
		});
	}

	@Override
	public void onGetAppInfo(final MyError err,final List<AppInfoItem> list) {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onGetAppInfo(err, list);
			}
		});
	}

}
