package com.maptool;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class MainActivity extends Activity implements OnClickListener,
		OnGetPoiSearchResultListener, OnGetRoutePlanResultListener,
		BaiduMap.OnMapClickListener, CloudListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private Button mPoiButton;
	private Button mLbsButton;
	private MapView mMapView;

	private BaiduMap mBaiduMap;
	private LatLng mLatLng;
	private LocationClient mLocationClient;
	private MyLocationListenner mMyLocationListenner;
	private boolean isFirstLocated = true;
	private PoiSearch mPoiSearch;
	public static PoiDetailResult mCurrentPoiDetailResult;
	// 搜索相关
	private RoutePlanSearch mRoutePlanSearch = null;
	private WalkingRouteLine mWalkingRouteLine = null;
	private OverlayManager mOverlayManager = null;
	private CloudManager mCloudManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setOnMapClickListener(this);

		mPoiButton = (Button) findViewById(R.id.poi);
		mPoiButton.setOnClickListener(this);
		mLbsButton = (Button) findViewById(R.id.lbs);
		mLbsButton.setOnClickListener(this);

		initLocation();
		initPoi();
		initRoute();
		initLbsCloud();
	}

	/**
	 * 定位初始化
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(this);
		mMyLocationListenner = new MyLocationListenner();
		mLocationClient.registerLocationListener(mMyLocationListenner);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	/**
	 * POI初始化
	 */
	private void initPoi() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
	}

	/**
	 * 路线初始化
	 */
	private void initRoute() {
		mRoutePlanSearch = RoutePlanSearch.newInstance();
		mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
	}

	/**
	 * 显示导航路线
	 */
	private void showRoutePlan() {
		// 设置起终点信息
		PlanNode startNode = PlanNode.withLocation(mLatLng);
		PlanNode endNode = PlanNode.withLocation(mCurrentPoiDetailResult
				.getLocation());
		mRoutePlanSearch.walkingSearch((new WalkingRoutePlanOption()).from(
				startNode).to(endNode));
	}

	/**
	 * 初始化LBS云
	 */
	private void initLbsCloud() {
		mCloudManager = CloudManager.getInstance();
		mCloudManager.init(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.poi:
			mPoiSearch.searchNearby((new PoiNearbySearchOption()).keyword("美食")
					.location(mLatLng).pageCapacity(20).radius(5000));
			break;
		case R.id.lbs:
//			NearbySearchInfo info = new NearbySearchInfo();
//			info.ak = "Fyi7fdQD1O7Co99TSt52m7Oo";
//			info.geoTableId = 99349;
//			info.radius = 30000;
//			info.location = mLatLng.longitude+","+mLatLng.latitude;
//			boolean cloudB = mCloudManager.nearbySearch(info);
//			Log.e(TAG, "{cloudB}"+cloudB+"{latitude}");
			
			Intent i = new Intent(MainActivity.this,MyActivity.class);
			startActivity(i);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		Log.e(TAG, "onGetPoiDetailResult");
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();

			/**
			 * // 暂且转到其他Activity中显示信息 mCurrentPoiDetailResult = result; Intent
			 * intent = new Intent(this, PoiInfoActivity.class); Bundle bundle =
			 * new Bundle(); startActivity(intent);
			 */
			mCurrentPoiDetailResult = result;
			showRoutePlan();

		}

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		Log.e(TAG, "onGetPoiResult");

		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		ArrayList<PoiInfo> poiInfos = (ArrayList<PoiInfo>) result.getAllPoi();
		for (int i = 0; i < poiInfos.size(); i++) {
			PoiInfo poiInfo = poiInfos.get(i);
			Log.e(TAG, poiInfo.address + ";" + poiInfo.name);
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLocated) {
				isFirstLocated = false;
				mLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				Log.e(TAG, "{location mLatLng.latitude}" + mLatLng.latitude);
				Log.e(TAG, "{location mLatLng.longitude}" + mLatLng.longitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(
						mLatLng, 16);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * POI图层
	 * 
	 * @author maxw
	 * @date 2015-4-8
	 * 
	 */
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			Log.e(TAG, "onPoiClick");
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	/**
	 * 路线图层
	 * 
	 * @author maxw
	 * @date 2015-4-9
	 * 
	 */
	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
		mPoiSearch.destroy();

		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onGetDrivingRouteResult");
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onGetTransitRouteResult");
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		Log.e(TAG, "onGetWalkingRouteResult");

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mWalkingRouteLine = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			mOverlayManager = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onMapClick");
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onMapPoiClick");
		return false;
	}

	
	@Override
	public void onGetDetailSearchResult(DetailSearchResult result, int arg1) {
		Log.e(TAG, "onGetDetailSearchResult");
		
		if (result != null) {
			if (result.poiInfo != null) {
				Toast.makeText(this, result.poiInfo.title,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this,
						"status:" + result.status, Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	@Override
	public void onGetSearchResult(CloudSearchResult result, int arg1) {
		Log.e(TAG, "onGetSearchResult");
		
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.e(TAG, "onGetSearchResult, result length: " + result.poiList.size());
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u); 
			
		}else{
			Log.d(TAG, ""+result.size);
		}
	}

	
}
