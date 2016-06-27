package com.maptool.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.maptool.R;
import com.maptool.common.L;
import com.maptool.lbs.MyPoiInfo;

public class InfoPopupView extends FrameLayout{
	
	public interface InfoPopupListener{
		public void onToClick(MyPoiInfo info);
		public void onStockoutClick(MyPoiInfo info);
	}

	TextView tvDevicePos;
	TextView tvAddredd;
	TextView tvStartTime;
	Button btnTo;
	Button btnStockout;
	
	InfoPopupListener mListener;
	MyPoiInfo mPoiInfo;
	
//	boolean isNearby = false;
	
	public InfoPopupView(Context context,MyPoiInfo info,boolean isNearby,InfoPopupListener listener) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.popup_info, this);
		
		mListener = listener;
		mPoiInfo = info;
		
		tvDevicePos = (TextView)findViewById(R.id.textView_machinePos);
		tvAddredd = (TextView)findViewById(R.id.textView_address);
//		tvStartTime = (TextView)findViewById(R.id.textView_startTime);
		btnTo = (Button)findViewById(R.id.button_to);
		btnStockout = (Button)findViewById(R.id.button_stockoout);
		
		if(isNearby){
			btnTo.setVisibility(View.GONE);
		}else{
			btnStockout.setVisibility(View.GONE);
		}
		
		// 显示数据
		tvDevicePos.setText(tvDevicePos.getText()+mPoiInfo.device_pos);
		tvAddredd.setText(tvAddredd.getText()+mPoiInfo.address);
		tvStartTime.setText(tvStartTime.getText()+mPoiInfo.start_time);
		
		btnTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onToClick(mPoiInfo);
			}
		});
		
		btnStockout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onStockoutClick(mPoiInfo);
			}
		});
	}
	
	
}
