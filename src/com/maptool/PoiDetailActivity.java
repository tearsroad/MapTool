package com.maptool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maptool.bean.SBItem;
import com.maptool.common.L;
import com.maptool.common.MyError;
import com.maptool.lbs.AppInfoItem;
import com.maptool.lbs.MyLBS;
import com.maptool.lbs.MyLBS.LBSInteractionListener;
import com.maptool.lbs.MyPoiInfo;
import com.maptool.lbs.StockoutPoiInfo;
import com.maptool.map.LBSHepler;
import com.maptool.util.ScreenUtils;
import com.maptool.util.ToolsUtil;
import com.maptool.view.SelectItemPopupWindow;
import com.maptool.view.adapter.SBAlertListAdapter;

public class PoiDetailActivity extends Activity {
	private Context mContext = this;
	private Button btnSend;
	private RelativeLayout rlffpz,rlfflx;
	private MyPoiInfo poiInfo;
	private TextView tvTitle,tvAddr,tvStartTime,tvEndTime;
	private boolean isNearby;
	LBSHepler mLBSHelper;
	Handler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_detail);
		poiInfo = (MyPoiInfo) getIntent().getSerializableExtra("position");
		isNearby = getIntent().getBooleanExtra("isNearby", false);
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				L.d("handleMessage");
			};
		};
		// 初始化lbs搜索
		mLBSHelper = new LBSHepler(mHandler, new MyMapInteractionListener());
		
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
		
		//显示上班时间，如果上午下班时间和下午上班时间一样，则只显示上班时间；
		//又一个为空值也只显示上班时间，不分上下午
		String start_time = ToolsUtil.ToDBC(poiInfo.start_time.trim());
		String end_time = ToolsUtil.ToDBC(poiInfo.end_time.trim());
		start_time = start_time.trim();
		end_time = end_time.trim();
		if(ToolsUtil.isStrNull(poiInfo.start_time)||ToolsUtil.isStrNull(poiInfo.end_time)){
			tvEndTime.setVisibility(View.GONE);
			String time = "";
			if(!ToolsUtil.isStrNull(poiInfo.start_time))
				time = start_time;
			if(!ToolsUtil.isStrNull(poiInfo.end_time))
				time = end_time;
			tvStartTime.setText("上班时间:  "+time);
		}else if(isTogether(start_time, end_time)){
			tvEndTime.setVisibility(View.GONE);
			String[] startTimes = start_time.split("-");
			String[] endTimes = end_time.split("-");
			String time = startTimes[0]+"-"+endTimes[1];
			tvStartTime.setText("上班时间:  "+time);
		}else{
			tvStartTime.setText("上午时间:  "+start_time);
			tvEndTime.setText("下午时间:  "+end_time);
		}
	}
	//09:00-12:00 
	//14:00-18:30
	/**
	 * 是否需要合并为上班时间
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private boolean isTogether(String startTime,String endTime){
		String[] startTimes = startTime.split("-");
		String[] endTimes = endTime.split("-");
		if(startTimes.length==2&&endTimes.length==2){
//			String[] starts = startTimes[1].split(":");
//			String[] ends = endTimes[1].split(":");
//			if(starts.length==2&&ends.length==2){
//				
//			}else{
//				return false;
//			}
			int result = endTimes[0].compareTo(startTimes[1]);
			if(result<=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	private void initView(){
		btnSend = (Button) findViewById(R.id.btn_shangbao);
		rlffpz = (RelativeLayout) findViewById(R.id.rl_ffpz);
		rlfflx = (RelativeLayout) findViewById(R.id.rl_fflx);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvAddr = (TextView) findViewById(R.id.tv_address);
		tvStartTime = (TextView) findViewById(R.id.tv_starttime);
		tvEndTime = (TextView) findViewById(R.id.tv_endtime);
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
			switch (v.getId()) {
			case R.id.btn_shangbao:
//				Toast.makeText(mContext, "isNearby:"+isNearby, Toast.LENGTH_LONG).show();
				if(isNearby)
					stockOut();
				else
					L.showToast("报歉，离在这个发放点100米内才能使用这个功能。", Toast.LENGTH_LONG);
				break;
			case R.id.rl_ffpz:
				SelectItemPopupWindow selectItemPopupWindow = new SelectItemPopupWindow(PoiDetailActivity.this
						, poiInfo.getFfpingzhong(),poiInfo.title);
				selectItemPopupWindow.showAtLocation(findViewById(R.id.layoutAll), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				selectItemPopupWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						ScreenUtils.backgroundAlpha(mContext, 1f);
					}
				});
				break;
			case R.id.rl_fflx:
				SelectItemPopupWindow selectItemPopupWindow2 = new SelectItemPopupWindow(PoiDetailActivity.this
						, poiInfo.getFfxingshi(),poiInfo.title);
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
	/**
	 * 自定义地图互动回调类 需要后台线程操作的回调都定义在改回调类中
	 */
	class MyMapInteractionListener implements LBSInteractionListener {
		@Override
		public void onGetNearbyGoods(MyError err, List<MyPoiInfo> list) {
			
		}

		@Override
		public void onReportStockout(MyError err) {
			if(err!=null){
				L.showToast(err.getShowMsg());
				return;
			}
			
			L.showToast("上报成功！");
		}

		@Override
		public void onGetStockout(MyError err, List<StockoutPoiInfo> list) {
			// 这个回调不对外
		}

		@Override
		public void onGetAppInfo(MyError err, List<AppInfoItem> list) {
			// TODO Auto-generated method stub
			
		}
	}
	private void stockOut(){
		//缺货上报，先判断是否已经上报
		mLBSHelper.isisStockout(poiInfo, new MyLBS.IsStockoutListener() {
			@Override
			public void onGetIsStockout(MyError err, final boolean isStockout) {
				if(err==null){
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if(isStockout){
								L.showToast("该地点已经上报");
							}else{
								showStockoutDialog(poiInfo);
							}
						}
					});
				}
			}
		});
	}
	private AlertDialog myDialog = null; 
	private SBAlertListAdapter mAdapter;
	private List<SBItem> sbList= new ArrayList<SBItem>();
	protected void showStockoutDialog(final MyPoiInfo info){
		myDialog = new AlertDialog.Builder(mContext).create();  
		myDialog.show(); 
        myDialog.getWindow().setContentView(R.layout.stock_alert_layout);  
        
        Window window = myDialog.getWindow() ;
        window.findViewById(R.id.btn_cancle)  
            .setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                myDialog.dismiss();  
            }  
        }); 
        window.findViewById(R.id.btn_shangbao)  
	        .setOnClickListener(new View.OnClickListener() {  
	        @Override  
	        public void onClick(View v) {  
	             
	            String reasonStr = "";
	            for(int i=0;i<sbList.size();i++){
	            	if(sbList.get(i).isSelected()){
	            		reasonStr=reasonStr+(i+1)+";";
	            	}
	            }
	            if(reasonStr.length()<1){
	            	L.showToast("请先选择你要上报的选项！");
	            }else{
	            	myDialog.dismiss(); 
//	            	L.showToast(reasonStr);
	            	mLBSHelper.reportStockout(info,reasonStr);
	            }
	            
	            
	        }  
        }); 
        ListView lv = (ListView) window.findViewById(R.id.lv_sblist) ;
        sbList.clear();
        sbList.addAll(getItems());
        mAdapter = new SBAlertListAdapter(mContext, sbList, mHandler2);
        lv.setAdapter(mAdapter);
         
		
		
		
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, android.R.style.Theme_Holo_Dialog));
//		builder.setTitle("选择原因");
//		builder.setItems(R.array.stockout_reason, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, final int which) {
//				new AlertDialog.Builder(mContext).setTitle("确认上报吗？")
//				.setIcon(android.R.drawable.ic_dialog_info)
//				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int w) {
//						// 点击“确认”后的操作
//						String reasonStr = mContext.getResources().getStringArray(R.array.stockout_reason)[which];
//						mLBSHelper.reportStockout(info,reasonStr);
//					}
//				})
//				.setNegativeButton("返回", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// 点击“返回”后的操作,这里不设置没有任何操作
//					}
//				}).show();
//			}
//		});
//		builder.setNegativeButton("取消", null);
//		builder.create().show();
	}
	private List<SBItem> getItems(){
		List<SBItem> sbItems=new ArrayList<SBItem>();
		String[] items = getResources().getStringArray(R.array.stockout_reason);
		for(int i=0;i<items.length;i++){
			SBItem item = new SBItem(false,items[i]);
			sbItems.add(item);
		}
		return sbItems;
	}
	private Handler mHandler2 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int position = msg.arg1;
			boolean isSelected = (Boolean) msg.obj;
			sbList.get(position).setSelected(isSelected);
			mAdapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
		
	};
}
