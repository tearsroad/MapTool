package com.maptool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.maptool.common.L;
import com.maptool.map.MapHelper;
import com.maptool.util.ScreenUtils;
import com.maptool.util.SystemStatusManager;
import com.maptool.util.ToolsUtil;

public class MapActivity extends Activity implements View.OnClickListener {
	private static final String PreferenceName = "mapinfo";
	MapHelper mMapHelper;
	Handler mHandler;
	Button btnChangeMapType;
	RadioGroup rgRadius;
	RadioButton rb500, rb1000, rb2000;
	// Button btnRefresh;
	ImageButton btnArticles;
	private UiSettings mUiSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// setContentView(R.layout.activity_map);

		L.setContext(this);

		// MapView mapView = (MapView)findViewById(R.id.bmapView);
		BaiduMapOptions option = new BaiduMapOptions();
		option.zoomControlsEnabled(false);
		MapView mapView = new MapView(this, option);
		BaiduMap mBaiduMap = mapView.getMap();
	    mUiSettings = mBaiduMap.getUiSettings();
	    mUiSettings.setCompassEnabled(false);
	    mUiSettings.setRotateGesturesEnabled(false);
	    mUiSettings.setOverlookingGesturesEnabled(false);
//	    Point arg0 = new Point(10, 60);
//	    mUiSettings.setCompassPosition(arg0);
		setContentView(mapView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = LayoutInflater.from(this).inflate(R.layout.activity_map,
				null);
		this.addContentView(view, params);

		LatLng lastPos = loadPos();
		mMapHelper = new MapHelper(this, mapView, mHandler, lastPos);

		btnChangeMapType = (Button) findViewById(R.id.button_changeMapType);
		btnChangeMapType.setOnClickListener(this);

		rb500 = (RadioButton) findViewById(R.id.rb_500);
		rb1000 = (RadioButton) findViewById(R.id.rb_1000);
		rb2000 = (RadioButton) findViewById(R.id.rb_2000);
		rgRadius = (RadioGroup) findViewById(R.id.radioGroup_radius);
		rgRadius.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				setRadioBtnTextColor(checkedId);
				if (rb500.getId() == checkedId) {
					
					mMapHelper.changeSearchRadius(500);
				} else if (rb1000.getId() == checkedId) {
					mMapHelper.changeSearchRadius(1000);
				} else if (rb2000.getId() == checkedId) {
					mMapHelper.changeSearchRadius(2000);
				}
			}
		});
		// btnRefresh = (Button) findViewById(R.id.btn_refresh);
		// btnRefresh.setOnClickListener(this);

		btnArticles = (ImageButton) findViewById(R.id.button_articles);
		btnArticles.setOnClickListener(this);
	}
	private void setRadioBtnTextColor(int checkedId){
		rb500.setTextColor(getResources().getColor(R.color.radio_noselect));
		rb1000.setTextColor(getResources().getColor(R.color.radio_noselect));
		rb2000.setTextColor(getResources().getColor(R.color.radio_noselect));
		if (rb500.getId() == checkedId) {
			rb500.setTextColor(getResources().getColor(R.color.radio_selected));
		} else if (rb1000.getId() == checkedId) {
			rb1000.setTextColor(getResources().getColor(R.color.radio_selected));
		} else if (rb2000.getId() == checkedId) {
			rb2000.setTextColor(getResources().getColor(R.color.radio_selected));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		savePos();
		mMapHelper.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapHelper.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapHelper.onDestroy();
	}
	//设置系统状态栏  
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)  
    private void setTranslucentStatus()   
    {  
        //判断版本是4.4以上  
        if (Build.VERSION.SDK_INT >= 19)  
        {  
            Window win = getWindow();  
            WindowManager.LayoutParams winParams = win.getAttributes();  
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;  
            winParams.flags |= bits;  
            win.setAttributes(winParams);  
              
            SystemStatusManager tintManager = new SystemStatusManager(this);  
            //打开系统状态栏控制  
            tintManager.setStatusBarTintEnabled(true);  
            tintManager.setStatusBarTintResource(0);//设置背景  
              
            View layoutAll = findViewById(R.id.layoutAll);  
            //设置系统栏需要的内偏移  
            layoutAll.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);  
        }  
    } 
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_changeMapType) {
			int currentMapType = mMapHelper.changeMapType();
			if (currentMapType == BaiduMap.MAP_TYPE_NORMAL) {
				btnChangeMapType.setBackgroundResource(R.drawable.normal_map1);
			} else {
				btnChangeMapType
						.setBackgroundResource(R.drawable.satellite_map1);
			}
		} else if (v.getId() == R.id.button_articles) {
			// Intent intent = new
			// Intent(MapActivity.this,ArticleListActivityCopy.class);
			Intent intent = new Intent(MapActivity.this,
					ArticleGridActivity.class);
			startActivity(intent);
		}
		// else if(v.getId() == R.id.btn_refresh){
		// if(rb500.isChecked()){
		// mMapHelper.changeSearchRadius(500);
		// }else if(rb1000.isChecked()){
		// mMapHelper.changeSearchRadius(1000);
		// }else if(rb2000.isChecked()){
		// mMapHelper.changeSearchRadius(2000);
		// }
		// }
	}

	private void savePos() {
		LatLng curPos = mMapHelper.getCurPos();
		if (curPos != null) {
			SharedPreferences preference = getSharedPreferences(PreferenceName,
					0);
			SharedPreferences.Editor editor = preference.edit();
			editor.putFloat("latitude", (float) curPos.latitude);
			editor.putFloat("longitude", (float) curPos.longitude);
			editor.commit();
		}
	}

	private LatLng loadPos() {
		SharedPreferences preference = getSharedPreferences(PreferenceName, 0);
		if (preference.contains("latitude")) {
			float latitude = preference.getFloat("latitude", 0);
			float longitude = preference.getFloat("longitude", 0);
			return new LatLng((double) latitude, (double) longitude);
		} else {
			return null;
		}
	}

	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("确认退出吗？")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“确认”后的操作
						MapActivity.this.finish();

					}
				})
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“返回”后的操作,这里不设置没有任何操作
					}
				}).show();
	}
}
