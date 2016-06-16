package com.maptool;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.maptool.lbs.MyLBS;
import com.maptool.lbs.PoiInfo;

public class MyActivity extends Activity {
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;

	LocationClient mLocClient;
	BitmapDescriptor mCurrentMarker = null;
	RoutePlanSearch mSearch;
	
	LatLng myLatLng = null;

	boolean isFirstLoc = true;// 是否首次定位
	// BitmapDescriptor bd = BitmapDescriptorFactory
	// .fromResource(R.drawable.icon_gcoding);

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// baidu sdk global init
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_my);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MyLBS.Message_NearGoods:
					List<PoiInfo> list = (List<PoiInfo>) msg.obj;
					if (list != null) {
						mBaiduMap.clear();
						LatLng ll;
						LatLngBounds.Builder builder = new LatLngBounds.Builder();

						for (PoiInfo info : list) {
							ll = new LatLng(info.latitude, info.longitude);
							BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
							Bundle b = new Bundle();
							b.putString("info", "hehe");
							b.putDouble("latitude", info.latitude);
							b.putDouble("longitude", info.longitude);
							OverlayOptions oo = new MarkerOptions().icon(bd).position(ll).extraInfo(b);
							mBaiduMap.addOverlay(oo);
							builder.include(ll);
						}

						LatLngBounds bounds = builder.build();
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
						mBaiduMap.animateMapStatus(u);
					}
					break;
				case MyLBS.Message_Error:
					Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mBaiduMap.setMyLocationEnabled(true);
		// mCurrentMarker = BitmapDescriptorFactory
		// .fromResource(R.drawable.ic_launcher);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, mCurrentMarker));

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null || mMapView == null)
					return;

				MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				mBaiduMap.setMyLocationData(locData);

				// if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				// }
			}
		});

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
			@Override
			public void onGetWalkingRouteResult(WalkingRouteResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
		            Toast.makeText(MyActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		        }
		        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
		            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
		            //result.getSuggestAddrInfo()
		            return;
		        }
		        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
		        	RouteLine route = result.getRouteLines().get(0);
		            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
		            mBaiduMap.setOnMarkerClickListener(overlay);
//		            routeOverlay = overlay;
		            overlay.setData(result.getRouteLines().get(0));
		            overlay.addToMap();
		            overlay.zoomToSpan();
		        }
			}
			@Override
			public void onGetTransitRouteResult(TransitRouteResult arg0) {
			}
			@Override
			public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
			}
		});
		
		// lsb search
		CloudManager.getInstance().init(new CloudListener() {
			@Override
			public void onGetSearchResult(CloudSearchResult result, int error) {
				Log.e("mzb", "onGetSearchResult  " + result + "  " + error);
				if (result != null && result.poiList != null && result.poiList.size() > 0) {
					Log.e("mzb", "poiList size: " + result.poiList.size());
					mBaiduMap.clear();
					LatLng ll;
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for (CloudPoiInfo info : result.poiList) {
						ll = new LatLng(info.latitude, info.longitude);
						BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
						OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
						mBaiduMap.addOverlay(oo);
						builder.include(ll);
					}
					LatLngBounds bounds = builder.build();
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
					mBaiduMap.animateMapStatus(u);
				}
			}

			@Override
			public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {

			}
		});
		// search current position nerayby
		searchNearby();
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				LatLng ll = marker.getPosition();
				Button button = new Button(getApplicationContext());
				button.setText(marker.getExtraInfo().getString("info"));
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mBaiduMap.hideInfoWindow();
					
						//路线规划
						PlanNode stNode = PlanNode.withLocation(myLatLng);
				        PlanNode enNode = PlanNode.withLocation(new LatLng(marker.getExtraInfo().getDouble("latitude"), marker.getExtraInfo().getDouble("longitude")));
				        mSearch.walkingSearch((new WalkingRoutePlanOption())
			                    .from(stNode)
			                    .to(enNode));
					}
				});
//				InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, 0, null);
				InfoWindow mInfoWindow = new InfoWindow(button, ll, -47);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
	}

	private void searchNearby() {
		MyLBS lbs = new MyLBS(100400, 100400);
//		lbs.searchNearGoods(1000, "116.440166,39.910638", handler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

}
