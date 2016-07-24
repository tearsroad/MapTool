package com.maptool.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.maptool.MapActivity;
import com.maptool.PoiDetailActivity;
import com.maptool.R;
import com.maptool.common.L;
import com.maptool.common.MyError;
import com.maptool.lbs.AppInfoItem;
import com.maptool.lbs.MyLBS.LBSInteractionListener;
import com.maptool.lbs.MyPoiInfo;
import com.maptool.lbs.StockoutPoiInfo;
import com.maptool.util.NetworkStateManager;
import com.maptool.util.ToolsUtil;
import com.maptool.view.MainSelectPicPopupWindow;

public class MapHelper {

	// handler消息
	public static final int MSG_ERROR = 100;

	// 基础与地图
	Activity mActivity;
	Handler mHandler;
	Handler mMainHandler;
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	
	// 普通图还是卫星图
	public int mCurMapType = BaiduMap.MAP_TYPE_NORMAL;

	// 定位
	LocationClient mLocClient;
	LatLng mCurLatLng = null; // 定位的当前位置
	boolean isFirstLoc = true; // 是否首次定位

	// 路径规划
	RoutePlanSearch mRouteSearch;
	OverlayManager mRouteOverlay; // 缓存当前的路线规划图层

	// lbs搜索
	LBSHepler mLBSHelper;
	List<MyPoiInfo> mPoiInfoCache; // 获取到周围物品后，缓存到这个列表中，用了判断是否距离用户很近
	int mSearchRadius = 500; // 搜索范围500,1000,2000
	int mReachRadius = 100;    // 自动弹出范围，目前是50米
//	boolean mIsShowNearByWindow = false; // 是否显示了消息框
//	MyPoiInfo mReachPoiInfo = null;  // 暂存附近机子（用来判断是否离开这个机子了）

	// 图标
	BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_nil);
	BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.position1);

	// 弹出框
	LayoutInflater mInflater;

	public MapHelper(Activity activity, MapView view, Handler handler,LatLng lastPos) {
		mActivity = activity;
		mMainHandler = handler;
		mMapView = view;
		mBaiduMap = mMapView.getMap();

		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				L.d("handleMessage");
			};
		};

		// 初始化界面相关
		mInflater = mActivity.getLayoutInflater();
		
		//初始化百度地图
		mBaiduMap.setMapType(mCurMapType);
		if(lastPos != null){
			//回复到上次定位位置
			//定义地图状态
	        MapStatus mMapStatus = new MapStatus.Builder()
	        .target(lastPos)
	        .zoom( ZoomUtil.get("200m"))
	        .build();
	        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


	        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
	        //改变地图状态
	        mBaiduMap.setMapStatus(mMapStatusUpdate);
		}

		// 初始化定位
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, mCurrentMarker,0,0));
		
		mLocClient = new LocationClient(mActivity);
		mLocClient.registerLocationListener(new LocationListener());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 初始化路径规划
		mRouteSearch = RoutePlanSearch.newInstance();
		mRouteSearch.setOnGetRoutePlanResultListener(new GetRoutePlanResultListener());

		// 初始化lbs搜索
		mLBSHelper = new LBSHepler(mHandler, new MyMapInteractionListener());

		// 点击地点标志监听
		mBaiduMap.setOnMarkerClickListener(new MarkerClickListener());
		
		//地图本身被点击的监听
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
			@Override
			public void onMapClick(LatLng arg0) {
				//点击地图时，隐藏弹出框
//				mBaiduMap.hideInfoWindow();
			}
		});
		
		//用户定位点被点击
		//说明：因为如果用户定位点与poi点很近的话，会优先判断为点击用户定位点而不是poi点，所以在点击用户定位点的时候也进行判断
		mBaiduMap.setOnMyLocationClickListener(new OnMyLocationClickListener() {
			@Override
			public boolean onMyLocationClick() {
				if(mPoiInfoCache != null){
					double minRadius = mReachRadius;
					MyPoiInfo minRadiusMyPoiInfo = null;
					for(MyPoiInfo info:mPoiInfoCache){
						LatLng ll = new LatLng(info.latitude, info.longitude);
						double dis = DistanceUtil.getDistance(mCurLatLng, ll);
						if(dis <= minRadius){
							//showInfoWindow(ll, info, true);
							minRadius = dis;
							minRadiusMyPoiInfo = info;
						}
					}
					if(minRadiusMyPoiInfo!=null){
						LatLng ll = new LatLng(minRadiusMyPoiInfo.latitude, minRadiusMyPoiInfo.longitude);
						showInfoWindow(ll, minRadiusMyPoiInfo, true);
					}
				}
				return false;
			}
		});
	}
	/**
	 * 设置我的位置的图标
	 */
	public void setMyLocationIcon(int draId,boolean isQuit){
		if(mBaiduMap==null)return;
		
		// 图标
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
	            .fromResource(draId);
		if(isQuit)return;
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, mCurrentMarker));
	}
	public LatLng getCurPos(){
		return mCurLatLng;
	}
	
	public int changeMapType(){
		if(mCurMapType == BaiduMap.MAP_TYPE_NORMAL){
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			mCurMapType = BaiduMap.MAP_TYPE_SATELLITE;
		}else{
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			mCurMapType = BaiduMap.MAP_TYPE_NORMAL;
		}
		return mCurMapType;
	}
	
	/**
	 * 更改搜索半径
	 */
	public void changeSearchRadius(int radius) {
		if(mCurLatLng != null){
			mBaiduMap.clear();
			mSearchRadius = radius;
//			mLBSHelper.searchNearby(mSearchRadius, mCurLatLng.longitude, mCurLatLng.latitude);
			searchNearby();
		}
	}
	
	/**
	 * 搜索周围物品(流程的第一部，所以做网络检查)
	 */
	protected void searchNearby(){
		NetworkStateManager.instance().init(mActivity);
		if(NetworkStateManager.instance().isNetworkConnected()){
			mLBSHelper.searchNearby(mSearchRadius, mCurLatLng.longitude, mCurLatLng.latitude);
		}else{
			L.showToast("没有网络连接");
		}
	}
	
	public void getAppInfo(){
		NetworkStateManager.instance().init(mActivity);
		if(NetworkStateManager.instance().isNetworkConnected()){
			mLBSHelper.getAppInfo();;
		}else{
//			L.showToast("没有网络连接");
		}
	}
	
	/**
	 * 
	 */
	public void searchRoute() {

	}

	/**
	 * 自定义地图互动回调类 需要后台线程操作的回调都定义在改回调类中
	 */
	class MyMapInteractionListener implements LBSInteractionListener {
		@Override
		public void onGetNearbyGoods(MyError err, List<MyPoiInfo> list) {
			if(err!=null){
				L.showToast(err.getShowMsg(),Toast.LENGTH_LONG);
				return;
			}
			
			if (list == null || list.size() == 0) {
				L.showToast("周围无计生用品");
				return;
			}
			L.showToast("搜索成功，您周围有" + list.size() + "个免费避孕药具发放点。");

			//把周围的物品缓存起来
			mPoiInfoCache = list;
			
			mBaiduMap.clear();
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(mCurLatLng);
			for (MyPoiInfo info : list) {
				LatLng ll = new LatLng(info.latitude, info.longitude);

				// 把poi信息放到marker中
				Bundle b = new Bundle();
				b.putSerializable("MyPoiInfo", info);
				MarkerOptions oo = new MarkerOptions().icon(bd).position(ll).extraInfo(b);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
			}

			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
		}

		@Override
		public void onReportStockout(MyError err) {
			if(err!=null){
				L.showToast(err.getShowMsg(),Toast.LENGTH_LONG);
				return;
			}
			
			L.showToast("缺货上报成功！",Toast.LENGTH_LONG);
		}

		@Override
		public void onGetStockout(MyError err, List<StockoutPoiInfo> list) {
			// 这个回调不对外
		}

		@Override
		public void onGetAppInfo(MyError err, List<AppInfoItem> list) {
//			L.showToast("onGetAppInfo！"+list.size()+";err:"+err);
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					AppInfoItem appinfo = list.get(i);
					if(ToolsUtil.getPackName(mActivity).equals(appinfo.packageName)
							&&appinfo.versionCode>ToolsUtil.getAppVersionCode(mActivity)){
						showAppInfoDialog(appinfo);
						break;
					}
				}
				
			}
			
		}
	}

	/**
	 * 定位回调类
	 * 
	 * @author mazhibin
	 */
	protected class LocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
				return;

			// 设置当前位置
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);

			// 记录到类变量
			mCurLatLng = new LatLng(location.getLatitude(), location.getLongitude());

			// 如果是第一次定位，移动地图，使当前位置为中心
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				mCurLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, ZoomUtil.get("200m"));
				mBaiduMap.animateMapStatus(u);

				// 第一次定位成功，搜索周围物品
				searchNearby();
			}
			// 修改为自定义marker
			
		    
//		    BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//	                .fromResource(R.drawable.current_position1_1);
//	        mBaiduMap
//	                .setMyLocationConfigeration(new MyLocationConfiguration(
//	                		null, true, mCurrentMarker));
			//判断附近是否有物品 
			//2015年5月11日  取消，因为不需要自动弹出了
			/*if(mPoiInfoCache != null){
				if(mReachPoiInfo==null){  
					for(MyPoiInfo info:mPoiInfoCache){
						LatLng ll = new LatLng(info.latitude, info.longitude);
						double dis = DistanceUtil.getDistance(mCurLatLng, ll);
						if(dis <= mReachRadius){
							mReachPoiInfo = info;
							L.showToast("您的周围有发放机哦！快去看看吧");
							showInfoWindow(ll, info, true);
						}
					}
				}else{  //已经显示了最近机子对话框，那么就判断是否还在范围内
					LatLng ll = new LatLng(mReachPoiInfo.latitude, mReachPoiInfo.longitude);
					double dis = DistanceUtil.getDistance(mCurLatLng, ll);
					if(dis>mReachRadius){
						mBaiduMap.hideInfoWindow();
						mReachPoiInfo = null;
					}
				}
				
			}*/
		}
	}

	/**
	 * 路径规划回调类
	 * 
	 * @author mazhibin
	 */
	protected class GetRoutePlanResultListener implements OnGetRoutePlanResultListener {
		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				L.showToast("抱歉，路线规划失败");
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
//				WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
				if(mRouteOverlay!=null){
					mRouteOverlay.removeFromMap();
				}
				mRouteOverlay = overlay;
				// mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {
		}

		@Override
		public void onGetBikingRouteResult(BikingRouteResult arg0) {
			// TODO Auto-generated method stub
			
		}

	}
	/**
	 * 隐藏路线信息
	 */
	public void removeRountInfo(){
		
		if (mRouteOverlay != null) {
			mRouteOverlay.removeFromMap();
		}else{
			Log.e("removeRountInfo", "mRouteOverlay is null");
		}
//		mBaiduMap.clear();
		mBaiduMap.hideInfoWindow();
	}
	/**
	 * 标记点击回调类
	 * 
	 * @author mazhibin
	 */
	protected class MarkerClickListener implements OnMarkerClickListener {
		@Override
		public boolean onMarkerClick(Marker marker) {
			//隐藏路线信息
			removeRountInfo();

			// 判断是否是我们自定义的图标
			// 点击有两种情况：1.点击的是远距离的目标，那么现实的对话框中的按钮是“到这去”
			// 2.点击的是在范围内的目标，对话框中的按钮是“缺货上报”
			Bundle b = marker.getExtraInfo();
			if (b != null && b.containsKey("MyPoiInfo")) {
				MyPoiInfo info = (MyPoiInfo) b.getSerializable("MyPoiInfo");
				LatLng ll = marker.getPosition();
				
				double dis = DistanceUtil.getDistance(mCurLatLng, ll);
				if(dis <= mReachRadius){
//					L.showToast("您的周围有发放机哦！快去看看吧");
					showInfoWindow(ll, info, true);
				}else{
					showInfoWindow(ll,info,false);
				}
			}
			return true;
		}
	}

	/**
	 * 位置弹出信息框事件回调类
	 */
	public class MyInfoPopupListener implements MainSelectPicPopupWindow.InfoPopupListener {
		@Override
		public void onToClick(MyPoiInfo info) {
			mBaiduMap.hideInfoWindow();

			// 路线规划
			PlanNode stNode = PlanNode.withLocation(mCurLatLng);
			PlanNode enNode = PlanNode.withLocation(new LatLng(info.latitude, info.longitude));
			mRouteSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
		}

		@Override
		public void onStockoutClick(MyPoiInfo info,boolean isNearby) {
			//缺货上报，先判断是否已经上报

			Intent intent = new Intent(mActivity,PoiDetailActivity.class);
			intent.putExtra("position", info);
			intent.putExtra("isNearby", isNearby);
			mActivity.startActivity(intent);
		}
	}
	
	protected void showStockoutDialog(final MyPoiInfo info){
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, android.R.style.Theme_Holo_Dialog));
		builder.setTitle("选择原因");
		builder.setItems(R.array.stockout_reason, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, final int which) {
				new AlertDialog.Builder(mActivity).setTitle("确认上报吗？")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int w) {
						// 点击“确认”后的操作
						String reasonStr = mActivity.getResources().getStringArray(R.array.stockout_reason)[which];
						mLBSHelper.reportStockout(info,reasonStr);
					}
				})
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“返回”后的操作,这里不设置没有任何操作
					}
				}).show();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	/**
	 * 显示弹出框
	 * @param isNearby
	 */
	protected void showInfoWindow(LatLng ll,MyPoiInfo info, boolean isNearby){
		((MapActivity)mActivity).showButtomRl(info, isNearby, new MyInfoPopupListener());;
//		MainSelectPicPopupWindow mainSelectPicPopupWindow = new MainSelectPicPopupWindow(mActivity, 
//				info, isNearby, new MyInfoPopupListener());
//		mainSelectPicPopupWindow.showAtLocation(mActivity.findViewById(R.id.layoutAll), 
//				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置  
	}
	private void showAppInfoDialog(final AppInfoItem item){
		final AlertDialog myDialog = new AlertDialog.Builder(mActivity).create(); 
		myDialog.show(); 
		myDialog.setCanceledOnTouchOutside(false);
		myDialog.setCancelable(false);
        myDialog.getWindow().setContentView(R.layout.appinfo_alert_layout);  
        
        Window window = myDialog.getWindow() ;
        Button btnLeft = (Button) window.findViewById(R.id.btn_left) ;
        btnLeft.setText(item.isForceUpdate?"退出应用":"以后再说");
        btnLeft.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                myDialog.dismiss();  
                if(item.isForceUpdate)
                	System.exit(0);
            }  
        }); 
        window.findViewById(R.id.btn_right)  
	        .setOnClickListener(new View.OnClickListener() {  
	        @Override  
	        public void onClick(View v) {  
	             
	             Uri uri = Uri.parse(item.url);          
	             Intent it = new Intent(Intent.ACTION_VIEW, uri);          
	             mActivity.startActivity(it);
	        }  
        }); 
        TextView versionName = (TextView) window.findViewById(R.id.tv_versionName) ;
        TextView history = (TextView) window.findViewById(R.id.tv_history) ;
        versionName.setText("新版本号："+item.versionName);
        history.setText(item.history);
//        history.setText("1.测试；\n2.测试；\n3.测试；\n4.测试；");
        
	}

	public void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
	}

	public void onResume() {
		mMapView.onResume();
	}

	public void onPause() {
		mMapView.onPause();
	}
	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
            return null;
        }

		@Override
		public int getLineColor() {
			// TODO Auto-generated method stub
			return mActivity.getResources().getColor(R.color.map_line_color);
		}

    }
}

/*
 * 百度地图缩放等级工具类 3-19
 * [2000km,1000km,500km,200km,100km,50km,25km,20km,10km,5km,2km,
 * 1km,500m,200m,100m,50m,20m]
 */
final class ZoomUtil {
	private static final String DESC_ARR[] = { "2000km", "1000km", "500km", "200km", "100km", "50km", "25km", "20km", "10km", "5km", "2km", "1km", "500m",
			"200m", "100m", "50m", "20m" };
	private static Map<String, Integer> descMap = new HashMap<String, Integer>();

	static {
		for (int i = 3; i <= 19; i++) {
			descMap.put(DESC_ARR[i - 3], i);
		}
	}

	public static int get(String desc) {
		return descMap.get(desc);
	}
	
}
