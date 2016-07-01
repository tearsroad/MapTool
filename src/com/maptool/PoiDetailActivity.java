package com.maptool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maptool.artical.Article;
import com.maptool.lbs.MyPoiInfo;
import com.maptool.util.ScreenUtils;
import com.maptool.view.SelectItemPopupWindow;

public class PoiDetailActivity extends Activity {
	private Context mContext = this;
	private Button btnSend;
	private RelativeLayout rlffpz,rlfflx;
	private MyPoiInfo poiInfo;
	private TextView tvTitle,tvAddr,tvTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_detail);
		poiInfo = (MyPoiInfo) getIntent().getSerializableExtra("position");
		initView();
		initData();
		btnSend.setOnClickListener(clickListener);
		rlffpz.setOnClickListener(clickListener);
		rlfflx.setOnClickListener(clickListener);
		findViewById(R.id.iv_back).setOnClickListener(clickListener);
	}
	private void initData() {
		if(poiInfo==null)finish();
		tvTitle.setText(poiInfo.title);
		tvAddr.setText(poiInfo.address);
		tvTime.setText("上午时间: "+ToDBC(poiInfo.start_time)+"\n"+
				"下午时间: "+ToDBC(poiInfo.end_time));
	}
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	private void initView(){
		btnSend = (Button) findViewById(R.id.btn_shangbao);
		rlffpz = (RelativeLayout) findViewById(R.id.rl_ffpz);
		rlfflx = (RelativeLayout) findViewById(R.id.rl_fflx);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvAddr = (TextView) findViewById(R.id.tv_address);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tvTitle.isSelected()){
					tvTitle.setSelected(false);
				}else{
					tvTitle.setSelected(true);
				}
			}
		});
		
	}
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_shangbao:
				
				break;
			case R.id.rl_ffpz:
				SelectItemPopupWindow selectItemPopupWindow = new SelectItemPopupWindow(PoiDetailActivity.this, poiInfo.getFfpingzhong());
				selectItemPopupWindow.showAtLocation(findViewById(R.id.layoutAll), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				selectItemPopupWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						ScreenUtils.backgroundAlpha(mContext, 1f);
					}
				});
				break;
			case R.id.rl_fflx:
				SelectItemPopupWindow selectItemPopupWindow2 = new SelectItemPopupWindow(PoiDetailActivity.this, poiInfo.getFfxingshi());
				selectItemPopupWindow2.showAtLocation(findViewById(R.id.layoutAll), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				selectItemPopupWindow2.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						ScreenUtils.backgroundAlpha(mContext, 1f);
					}
				});
				break;
			case R.id.iv_back:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
}
