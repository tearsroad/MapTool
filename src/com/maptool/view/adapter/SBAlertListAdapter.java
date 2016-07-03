package com.maptool.view.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.maptool.R;
import com.maptool.bean.SBItem;
import com.maptool.common.L;


public class SBAlertListAdapter extends BaseAdapter {
	List<SBItem> items;
	Context context;
	private Handler mHandler;
	public SBAlertListAdapter(Context context, List<SBItem> items,Handler handler) {
		this.context = context;
		this.items = items;
		this.mHandler = handler;
		
	}
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    ViewHolder viewHolder = null;
	    if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_alert_sb, null);          
            viewHolder=new ViewHolder();
            viewHolder.rb_sb = (CheckBox)convertView.findViewById(R.id.radiobtn);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        } 
	    SBItem item = items.get(position);
        viewHolder.rb_sb.setText(item.getTitle());
        viewHolder.rb_sb.setChecked(item.isSelected());
        viewHolder.rb_sb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				L.e("checkchange:"+position+"---"+isChecked);
				items.get(position).setSelected(isChecked);
			}
		});
//        viewHolder.rb_sb.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				viewHolder.rb_sb.setSelected(!item.isSelected());
//			}
//		});
//        viewHolder.tv_manager.setText(item.getImgLink());
        return convertView;
	}
	class ViewHolder {
		CheckBox rb_sb;
        LinearLayout ll_parent;
	}

}
