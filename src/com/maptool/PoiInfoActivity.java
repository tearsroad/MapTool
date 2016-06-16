package com.maptool;

import java.text.MessageFormat;

import com.baidu.mapapi.search.poi.PoiDetailResult;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PoiInfoActivity extends Activity {

	private TextView mPoiInfoTextView;
	private PoiDetailResult mPoiDetailResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poiinfo);

		mPoiDetailResult = MainActivity.mCurrentPoiDetailResult;

		mPoiInfoTextView = (TextView) findViewById(R.id.tv_poiinfo);

		String poiInfo = MessageFormat.format("POI地址:{0}\nPOI名称:{1}",
				mPoiDetailResult.getAddress(), mPoiDetailResult.getName());
		mPoiInfoTextView.setText(poiInfo);
	}
}
