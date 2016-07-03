package com.maptool.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.maptool.R;
import com.maptool.lbs.MyPoiInfo;
import com.maptool.view.InfoPopupView.InfoPopupListener;

public class MainSelectPicPopupWindow extends PopupWindow {
	TextView tvDevicePos;
	TextView tvAddredd;
	Button btnTo;
	Button btnStockout;
	
	InfoPopupListener mListener;
	MyPoiInfo mPoiInfo;
	private View mView;  
	
	public interface InfoPopupListener{
		public void onToClick(MyPoiInfo info);
		public void onStockoutClick(MyPoiInfo info,boolean isNearby);
	}
  
    public MainSelectPicPopupWindow(Context context,MyPoiInfo info,final boolean isNearby,InfoPopupListener listener) {  
        super(context);  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mView = inflater.inflate(R.layout.popup_info, null);
		
		mListener = listener;
		mPoiInfo = info;
		
		tvDevicePos = (TextView)mView.findViewById(R.id.textView_machinePos);
		tvAddredd = (TextView)mView.findViewById(R.id.textView_address);
		btnTo = (Button)mView.findViewById(R.id.button_to);
		btnStockout = (Button)mView.findViewById(R.id.button_stockoout);
		
//		if(isNearby){
//			btnTo.setVisibility(View.GONE);
//		}else{
//			btnStockout.setVisibility(View.GONE);
//		}
		
		// 显示数据
		tvDevicePos.setText(mPoiInfo.title);
		String ffxingshi = mPoiInfo.getFfxingshiMsg();
		if(ffxingshi==null)
			tvAddredd.setText("");
		else
			tvAddredd.setText("（"+ffxingshi+"）");
//		tvAddredd.setText(tvAddredd.getText()+mPoiInfo.address);
		
		btnTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onToClick(mPoiInfo);
				dismiss();
			}
		});
		
		btnStockout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onStockoutClick(mPoiInfo,isNearby);
				dismiss();
			}
		});
        //设置SelectPicPopupWindow的View  
        this.setContentView(mView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.FILL_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        //设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.PopupAnimation);  
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0x00000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);  
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
        mView.setOnTouchListener(new OnTouchListener() {  
              
            public boolean onTouch(View v, MotionEvent event) {  
                  
                int height = mView.findViewById(R.id.pop_layout).getTop();  
                int y=(int) event.getY();  
                if(event.getAction()==MotionEvent.ACTION_UP){  
                    if(y<height){  
                        dismiss();  
                    }  
                }                 
                return true;  
            }  
        });  
  
    }  
}
