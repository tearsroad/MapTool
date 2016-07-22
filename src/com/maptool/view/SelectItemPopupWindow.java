package com.maptool.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.maptool.R;
import com.maptool.artical.Article;
import com.maptool.lbs.FFMsgItem;
import com.maptool.lbs.MyPoiInfo;
import com.maptool.util.ScreenUtils;
import com.maptool.view.adapter.SelectListAdapter;

public class SelectItemPopupWindow extends PopupWindow {
	private ListView lvSelects;
	private List<FFMsgItem> mList;
	private View mView;  
	private Context mContext;
	private String mTitle;
  
    public SelectItemPopupWindow(Context context,List<FFMsgItem> mList,String title) {  
        super(context);  
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mView = inflater.inflate(R.layout.popup_item_info, null);
        this.mTitle = title;
		this.mList = mList;
		lvSelects = (ListView) mView.findViewById(R.id.lv_selects);
		mView.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		SelectListAdapter adapter = new SelectListAdapter(context, mList,handler,this.mTitle);
		lvSelects.setAdapter(adapter);
        //设置SelectPicPopupWindow的View  
        this.setContentView(mView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.FILL_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.FILL_PARENT);  
        //设置SelectPicPopupWindow弹出窗体可点击  
//        this.setFocusable(true);  
        //设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.PopupAnimation);  
        
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0x00000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw); 
        this.update();
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        ScreenUtils.backgroundAlpha(mContext,0.4f);
        this.setOutsideTouchable(false);
        this.setFocusable(true);
        this.setTouchable(true);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
        mView.setOnTouchListener(new OnTouchListener() {  
              
            public boolean onTouch(View v, MotionEvent event) {  
                  
                int height = mView.findViewById(R.id.pop_layout).getTop();  
                int y=(int) event.getY();  
                if(event.getAction()==MotionEvent.ACTION_UP){  
                    if(y<height){  
//                        dismiss();  
                    }  
                }                 
                return false;  
            }  
        });  
        
        this.setTouchInterceptor(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
					return true;
				}
				return false;
			}
		});
  
    }  
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
//			dismiss();
			super.handleMessage(msg);
		}
    	
    };
    
}
