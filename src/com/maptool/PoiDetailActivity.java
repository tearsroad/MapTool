package com.maptool;

import com.maptool.map.MapHelper.MyInfoPopupListener;
import com.maptool.view.MainSelectPicPopupWindow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PoiDetailActivity extends Activity {
	private Button btnSend;
	private RelativeLayout rlffpz,rlfflx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_detail);
		initView();
		btnSend.setOnClickListener(clickListener);
		rlffpz.setOnClickListener(clickListener);
		rlfflx.setOnClickListener(clickListener);
	}
	private void initView(){
		btnSend = (Button) findViewById(R.id.btn_shangbao);
		rlffpz = (RelativeLayout) findViewById(R.id.rl_ffpz);
		rlfflx = (RelativeLayout) findViewById(R.id.rl_fflx);
	}
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_shangbao:
				
				break;
			case R.id.rl_ffpz:
				MainSelectPicPopupWindow mainSelectPicPopupWindow = new MainSelectPicPopupWindow(mActivity, info, isNearby, new MyInfoPopupListener());
				mainSelectPicPopupWindow.showAtLocation(mActivity.findViewById(R.id.layoutAll), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置  

				break;
			case R.id.rl_fflx:
				
				break;
			default:
				break;
			}
		}
	};
}
